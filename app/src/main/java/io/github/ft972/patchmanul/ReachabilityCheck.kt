package io.github.ft972.patchmanul

import android.os.Handler
import android.os.Looper
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Prueft eine Handvoll bekannter Ziele darauf, ob dort ein Geraet antwortet -
 * die Grundlage der roten und gruenen Markierung in beiden Listen.
 *
 * Anders als beim Suchlauf sind die Zeitgrenzen hier grosszuegig: Es geht um
 * eine ueberschaubare Zahl von Zielen, und ein Board, das gerade neu startet,
 * soll nicht vorschnell als tot gelten.
 *
 * **Ein Ziel kann ein Name statt einer Adresse sein.** Aufgeloest wird deshalb
 * hier, nicht erst in der HTTP-Verbindung: So steht die IP hinterher fest und
 * kann in den Favoriten nachgetragen werden. Antwortet der bevorzugte Weg
 * nicht, wird der zweite probiert - das faengt beide Ausfaelle ab, den Wechsel
 * der Adresse ebenso wie den des Namens.
 */
class ReachabilityCheck {

    /**
     * Was geprueft werden soll. [key] ist der Schluessel, unter dem die Zeile
     * ihr Ergebnis erwartet; [fallback] darf leer sein.
     */
    data class Target(
        val key: String,
        val preferred: String,
        val fallback: String
    )

    /**
     * [board] ist null, wenn keiner der beiden Wege geantwortet hat. Sonst sagt
     * [reached], ueber welches Ziel es ging, und [address] nennt die IP
     * dahinter - bei einem Namen die aufgeloeste.
     */
    data class Result(
        val board: BoardInfo?,
        val reached: String,
        val address: String
    )

    private val main = Handler(Looper.getMainLooper())
    private var pool: ExecutorService? = null

    /**
     * [onResult] kommt je Ziel einmal, [onFinished] am Ende. Beide Rueckrufe
     * laufen auf dem Hauptthread.
     */
    fun run(
        targets: List<Target>,
        onResult: (Target, Result) -> Unit,
        onFinished: () -> Unit
    ) {
        cancel()

        if (targets.isEmpty()) {
            onFinished()
            return
        }

        val executor = Executors.newFixedThreadPool(targets.size.coerceAtMost(MAX_THREADS))
        pool = executor
        val open = AtomicInteger(targets.size)

        for (target in targets) {
            executor.execute {
                val result = reach(target)
                main.post {
                    if (pool !== executor) return@post

                    onResult(target, result)
                    if (open.decrementAndGet() == 0) {
                        pool = null
                        onFinished()
                    }
                }
            }
        }

        executor.shutdown()
    }

    fun cancel() {
        pool?.shutdownNow()
        pool = null
    }

    /**
     * Erst der bevorzugte Weg, dann der andere.
     *
     * Der Rueckfall kostet im schlechtesten Fall die doppelte Zeit - er laeuft
     * aber nur fuer Ziele, die ohnehin schon nicht geantwortet haben, und jedes
     * Ziel hat seinen eigenen Thread.
     */
    private fun reach(target: Target): Result {
        try_(target.preferred)?.let { return it }
        if (target.fallback.isNotBlank()) try_(target.fallback)?.let { return it }
        return Result(null, "", "")
    }

    /** Der Unterstrich haelt den Namen von Kotlins try fern. */
    private fun try_(destination: String): Result? {
        if (destination.isBlank()) return null
        val address = resolve(destination) ?: return null
        val board = BoardProbe.probe(address, CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS) ?: return null
        return Result(board, destination, address)
    }

    private companion object {
        /**
         * Begrenzt die gleichzeitigen Anfragen, nicht die Zahl der Ziele: Sind
         * es mehr, laufen sie in einer zweiten Welle. Ausgelegt darauf, dass
         * rund zwanzig Favoriten in einem Rutsch durchgehen.
         */
        const val MAX_THREADS = 20
        const val CONNECT_TIMEOUT_MS = 1500
        const val READ_TIMEOUT_MS = 2500

        /**
         * Eine Zahlenadresse wird hier nur zerlegt, das kostet nichts. Ein Name
         * geht an den DNS-Server - **ohne Zeitgrenze**, die kennt InetAddress
         * nicht. Die Zeitgrenzen oben decken nur die HTTP-Verbindung ab.
         */
        fun resolve(destination: String): String? = try {
            InetAddress.getAllByName(destination)
                .firstOrNull { it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            null
        }
    }
}
