package io.github.ft972.patchmanul

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import java.net.Inet4Address
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Sucht Geraete, indem er jede Adresse des eigenen Subnetzes einmal anfragt.
 * Treffer werden gemeldet, sobald sie da sind - die Liste fuellt sich also
 * waehrend der Suche und nicht erst am Ende.
 *
 * Der Weg ueber mDNS waere bequemer, taugt hier aber nicht: Android loest
 * .local-Namen von sich aus nicht auf, und ob die Firmware ihren Responder
 * ueberhaupt betreibt, haengt an ihrer Einstellung. Der Sweep traegt in jedem
 * Fall.
 */
class NetworkScanner(context: Context) {

    interface Listener {
        fun onBoardFound(board: BoardInfo)

        /** Nach jeder geprueften Adresse; [done] zaehlt bis [total]. */
        fun onProgress(done: Int, total: Int)

        /** [subnet] ist null, wenn das Handy keine IPv4-Adresse im WLAN hat. */
        fun onFinished(scanned: Int, subnet: String?)
    }

    /**
     * Was ueber das eigene Netz bekannt ist, bevor gesucht wird - Grundlage
     * fuer die Warnung vor einem langen Suchlauf.
     */
    data class Survey(
        /** Die tatsaechliche Praefixlaenge des WLANs. */
        val prefixLength: Int,
        /** Adressen im eigenen /24 (oder enger, falls das Netz kleiner ist). */
        val narrowedHostCount: Int,
        /** Adressen im vollen Netz, oder 0, wenn es dafuer zu gross ist. */
        val fullHostCount: Int
    ) {
        /** Ist das Netz groesser als /24, muss der Nutzer entscheiden. */
        val needsChoice: Boolean get() = prefixLength < NARROW_PREFIX
    }

    private val appContext = context.applicationContext
    private val settings = SettingsStore(context)
    private val main = Handler(Looper.getMainLooper())
    private var pool: ExecutorService? = null

    val isRunning: Boolean
        get() = pool != null

    /** Ohne Netzverbindung null. Fragt nichts ab, rechnet nur. */
    fun survey(): Survey? {
        val own = localAddress() ?: return null
        val real = own.prefixLength

        val narrowed = hostCount(maxOf(real, NARROW_PREFIX).coerceAtMost(MAX_PREFIX))
        val full = if (real < MIN_FULL_PREFIX) 0 else hostCount(real.coerceAtMost(MAX_PREFIX))

        return Survey(prefixLength = real, narrowedHostCount = narrowed, fullHostCount = full)
    }

    /**
     * [fullRange] durchsucht das ganze WLAN statt nur des eigenen /24. Das ist
     * die Ausnahme und wird nur nach ausdruecklicher Bestaetigung gesetzt.
     */
    fun start(listener: Listener, fullRange: Boolean = false) {
        if (isRunning) return

        val range = localRange(fullRange)
        if (range == null || range.addresses.isEmpty()) {
            listener.onFinished(0, range?.description)
            return
        }

        // Einmal je Lauf gelesen, nicht je Adresse: Alle 253 Anfragen eines
        // Laufs sollen dieselbe Zeitgrenze haben, auch wenn nebenher etwas
        // umgestellt wird.
        val connectTimeout = settings.scanTimeoutMs
        val readTimeout = readTimeoutFor(connectTimeout)

        val executor = Executors.newFixedThreadPool(THREADS)
        pool = executor
        val total = range.addresses.size
        val open = AtomicInteger(total)
        val done = AtomicInteger(0)

        for (address in range.addresses) {
            executor.execute {
                val board = BoardProbe.probe(address, connectTimeout, readTimeout)
                main.post {
                    // Ein abgebrochener Lauf darf die Liste nicht mehr fuellen.
                    if (pool !== executor) return@post

                    if (board != null) listener.onBoardFound(board)
                    listener.onProgress(done.incrementAndGet(), total)

                    if (open.decrementAndGet() == 0) {
                        pool = null
                        listener.onFinished(total, range.description)
                    }
                }
            }
        }

        // Keine weiteren Aufgaben annehmen; die eingereihten laufen zu Ende.
        executor.shutdown()
    }

    fun cancel() {
        pool?.shutdownNow()
        pool = null
    }

    /** Wie lange ein Lauf ueber [hosts] Adressen ungefaehr dauert, in Sekunden. */
    fun estimatedSeconds(hosts: Int): Int {
        if (hosts <= 0) return 0
        // Fuer jede tote Adresse wird die Verbindungszeitgrenze voll abgewartet;
        // THREADS davon laufen gleichzeitig.
        val waves = (hosts + THREADS - 1) / THREADS
        return maxOf(1, (waves * settings.scanTimeoutMs) / 1000)
    }

    private data class Range(val addresses: List<String>, val description: String)

    private data class Local(val value: Int, val prefixLength: Int)

    private fun localAddress(): Local? {
        val manager = appContext.getSystemService(ConnectivityManager::class.java) ?: return null
        val network = manager.activeNetwork ?: return null
        val properties = manager.getLinkProperties(network) ?: return null

        val link = properties.linkAddresses.firstOrNull {
            val address = it.address
            address is Inet4Address && !address.isLoopbackAddress && !address.isLinkLocalAddress
        } ?: return null

        return Local(toInt(link.address as Inet4Address), link.prefixLength)
    }

    private fun hostCount(prefix: Int): Int = (1 shl (32 - prefix)) - 2

    private fun localRange(fullRange: Boolean): Range? {
        val own = localAddress() ?: return null

        // Ohne ausdrueckliche Ansage wird nicht unter /24 gesucht: Ein /16
        // waeren 65534 Adressen, also Minuten statt Sekunden. Nach oben begrenzt
        // /30 den Sonderfall einer Punkt-zu-Punkt-Strecke.
        val floor = if (fullRange) MIN_FULL_PREFIX else NARROW_PREFIX
        val prefix = own.prefixLength.coerceIn(floor, MAX_PREFIX)

        val base = own.value and (-1 shl (32 - prefix))
        val hosts = hostCount(prefix)

        val addresses = ArrayList<String>(hosts)
        for (offset in 1..hosts) {
            val value = base + offset
            if (value == own.value) continue
            addresses.add(toText(value))
        }
        return Range(addresses, "${toText(base)}/$prefix")
    }

    private fun toInt(address: Inet4Address): Int {
        val bytes = address.address
        return ((bytes[0].toInt() and 0xFF) shl 24) or
                ((bytes[1].toInt() and 0xFF) shl 16) or
                ((bytes[2].toInt() and 0xFF) shl 8) or
                (bytes[3].toInt() and 0xFF)
    }

    private fun toText(value: Int): String =
        "${(value ushr 24) and 0xFF}.${(value ushr 16) and 0xFF}." +
                "${(value ushr 8) and 0xFF}.${value and 0xFF}"

    companion object {
        /** Der Normalfall: nur das eigene /24. */
        const val NARROW_PREFIX = 24

        /**
         * Weiter als /16 wird auch auf Wunsch nicht gesucht. Ein /8 waeren
         * ueber sechzehn Millionen Adressen - das laeuft tagelang.
         */
        const val MIN_FULL_PREFIX = 16

        private const val MAX_PREFIX = 30

        /**
         * Gleichzeitige Anfragen. Alle warten nur auf Netz, belegen also kaum
         * Rechenzeit; 254 Adressen sind damit in wenigen Sekunden durch.
         */
        private const val THREADS = 40

        /**
         * Die Verbindungszeitgrenze bestimmt die Dauer der Suche - fuer jede
         * tote Adresse wird sie voll abgewartet. Sie steht deshalb nicht mehr
         * fest hier, sondern in den Einstellungen: Vorgabe ist der bisherige
         * Wert, nach oben geht es bis zum Anderthalbfachen, siehe
         * SettingsStore.SCAN_TIMEOUT_VALUES.
         *
         * Der Lesezeitraum waechst im selben Verhaeltnis mit. Die 2,5 zu 1
         * stammen von den bis 1.4.0 fest verdrahteten 600 und 1500 ms.
         */
        private fun readTimeoutFor(connectTimeoutMs: Int): Int = connectTimeoutMs * 5 / 2
    }
}
