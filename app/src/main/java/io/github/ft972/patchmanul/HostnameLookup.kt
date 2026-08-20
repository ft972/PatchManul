package io.github.ft972.patchmanul

import android.os.Handler
import android.os.Looper
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Was der DNS-Server zu einer gefundenen Adresse hergibt.
 *
 * [fqdn] ist der vollstaendige Name **zum Verbinden** - die Domaene gehoert
 * dazu, denn ob Android die Suchdomaene von selbst anhaengt, ist nicht
 * zugesichert. [label] ist derselbe Name ohne Domaene, nur zum Anzeigen.
 */
data class HostnameInfo(
    val fqdn: String,
    val label: String,
    /** Die Gegenprobe ist aufgegangen: Der Name zeigt auf genau diese Adresse. */
    val verified: Boolean
)

/**
 * Fragt zu einer IP-Adresse den Namen beim DNS-Server nach - die Grundlage fuer
 * den Namensvorschlag und fuer die Wahl des Wegs beim Uebernehmen in die
 * Favoriten.
 *
 * Der Name kommt vom Router, nicht vom Geraet: Die Firmware meldet ihren
 * Hostnamen per DHCP an, und ob daraus ein aufloesbarer Name wird, entscheidet
 * der Router. Manche pflegen eine Rueckwaertszone, viele einfachere nicht.
 *
 * **Rueckwaerts genuegt nicht.** Der Namensvorschlag braucht nur die Richtung
 * IP -> Name (PTR). Wer sich spaeter ueber den Namen verbinden will, braucht die
 * Gegenrichtung Name -> IP (A) - das ist eine andere DNS-Funktion, und ein
 * Router kann die eine beherrschen und die andere nicht. Deshalb wird jeder
 * gefundene Name gleich gegengeprobt; nur ein Name, der auf dieselbe Adresse
 * zurueckzeigt, taugt als Merkmal.
 */
class HostnameLookup {

    private val main = Handler(Looper.getMainLooper())
    private var pool: ExecutorService? = null

    /** Fuer die einmalige Probe aus dem Favoriten-Dialog, getrennt vom Suchlauf. */
    private var checkPool: ExecutorService? = null

    /**
     * [onResult] kommt je Adresse einmal auf dem Hauptthread, mit null, wenn
     * der DNS-Server keinen Namen kennt.
     */
    fun run(addresses: List<String>, onResult: (String, HostnameInfo?) -> Unit) {
        // Nur den eigenen Pool abraeumen: Eine laufende Gegenprobe aus dem
        // Favoriten-Dialog geht einen Suchlauf nichts an.
        pool?.shutdownNow()
        pool = null

        if (addresses.isEmpty()) return

        val executor = Executors.newFixedThreadPool(addresses.size.coerceAtMost(MAX_THREADS))
        pool = executor
        val open = AtomicInteger(addresses.size)

        for (address in addresses) {
            executor.execute {
                val info = lookup(address)
                main.post {
                    if (pool !== executor) return@post

                    onResult(address, info)
                    if (open.decrementAndGet() == 0) pool = null
                }
            }
        }

        executor.shutdown()
    }

    /**
     * Die Gegenprobe zu einem von Hand eingetippten Namen: Laesst er sich
     * ueberhaupt aufloesen?
     *
     * Verglichen wird hier mit **keiner** erwarteten Adresse - beim Anlegen von
     * Hand gibt es oft noch keine. Ein Fehlschlag ist deshalb eine Warnung, kein
     * Grund zur Ablehnung: Das Geraet koennte auch schlicht gerade aus sein.
     */
    fun verify(name: String, onResult: (Boolean) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        checkPool = executor
        executor.execute {
            val ok = resolve(name) != null
            main.post { if (checkPool === executor) onResult(ok) }
        }
        executor.shutdown()
    }

    fun cancel() {
        pool?.shutdownNow()
        pool = null
        checkPool?.shutdownNow()
        checkPool = null
    }

    private fun lookup(address: String): HostnameInfo? {
        return try {
            // getByName() auf eine Zahlenadresse fragt noch nichts nach, es
            // zerlegt nur den Text. Erst canonicalHostName loest rueckwaerts
            // auf; ohne PTR-Eintrag gibt es die Adresse unveraendert zurueck.
            val resolved = InetAddress.getByName(address).canonicalHostName
            if (resolved.isNullOrBlank() || resolved == address) return null

            val fqdn = resolved.trimEnd('.')
            val label = fqdn.substringBefore('.')
            if (label.isBlank()) return null

            // "bruecke.fritz.box" -> zeigt der Name auch wirklich hierher?
            HostnameInfo(fqdn, label, resolve(fqdn) == address)
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        /** Gleichzeitige Abfragen; mehr Adressen laufen in einer zweiten Welle. */
        const val MAX_THREADS = 20

        /**
         * Name -> IPv4. Nur die vierte Fassung, wie ueberall in dieser App: Eine
         * IPv6-Antwort waere ein Ziel, das der Erreichbarkeitstest nicht
         * ansprechen kann.
         *
         * **Ohne Zeitgrenze**, weil InetAddress keine anbietet. Jede Abfrage
         * haengt an ihrem eigenen Thread, ein zaeher DNS-Server haelt also nur
         * die eine Zeile auf.
         */
        fun resolve(name: String): String? = try {
            InetAddress.getAllByName(name)
                .firstOrNull { it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            null
        }
    }
}
