package io.github.ft972.patchmanul

import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL

/**
 * Erkennt ein Geraet an der Startseite, die seine Firmware ausliefert.
 *
 * **Warum die Startseite und kein eigener Endpunkt:** Die Firmware registriert
 * genau zwei Dinge - die eingebetteten Dateien unter / und einen WebSocket
 * unter /ws. Jeder andere Pfad wird mit 404 beantwortet, und es gibt keinen
 * Pfad, der etwas ueber das Geraet aussagt: keine Kennung, keinen Namen, keine
 * Version. Alles, was die Weboberflaeche kann, laeuft ueber den WebSocket,
 * und dafuer haette die App weder Client noch Bibliothek.
 *
 * Bleibt die Seite selbst. Erkannt wird sie an zwei Zeichenketten aus ihrem
 * Kopfbereich - der Preset-Auswahl, die es in dieser Form nur dort gibt.
 * Router, Drucker, NAS und Kameras im selben Netz fallen durch: Sie antworten
 * entweder gar nicht, mit einem anderen Inhaltstyp oder mit einer Seite, in der
 * diese Merkmale nicht vorkommen.
 *
 * **Die beiden Merkmale sind Protokollwerte, keine Bezeichnungen.** Sie stehen
 * so im HTML der fremden Firmware und muessen Zeichen fuer Zeichen passen.
 * Aendert die Firmware ihre Seite an dieser Stelle, findet die App nichts mehr
 * - das ist der Preis dafuer, dass es keinen dafuer gedachten Endpunkt gibt.
 * Sie sind bewusst so gewaehlt, dass sie **keinen Produktnamen** enthalten;
 * der Seitentitel taete es und scheidet deshalb aus.
 */
object BoardProbe {

    /**
     * Beide muessen vorkommen. Sie stehen in den ersten anderthalb Kilobyte der
     * Seite, also weit innerhalb dessen, was hier ueberhaupt gelesen wird.
     */
    private val MARKERS = listOf("id=\"set_preset\"", "onPresetChange(")

    /**
     * So weit wird in die Antwort hineingelesen, nicht weiter. Die Seite selbst
     * ist ein Vielfaches davon gross - gebraucht wird nur ihr Anfang, und die
     * Grenze schuetzt zugleich davor, dass ein fremder Server auf Port 80 die
     * App mit einem endlosen Body beschaeftigt.
     */
    private const val MAX_BODY_BYTES = 8 * 1024

    fun probe(address: String, connectTimeoutMs: Int, readTimeoutMs: Int): BoardInfo? {
        var connection: HttpURLConnection? = null
        try {
            // Proxy.NO_PROXY ist Absicht: Ein am WLAN eingetragener Proxy wuerde
            // die Anfrage aus dem lokalen Netz heraus schicken, wo das Geraet
            // nicht erreichbar ist.
            val url = URL("http", address, 80, "/")
            connection = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMs
            connection.readTimeout = readTimeoutMs
            connection.useCaches = false
            // Eine Umleitung fuehrt hier nirgendwohin: Die Firmware liefert ihre
            // Seite unmittelbar aus. Wer umleitet, ist etwas anderes.
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("Accept", "text/html")
            connection.setRequestProperty("Connection", "close")

            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null

            // Frueher Filter: Die Firmware setzt den Typ ausdruecklich. Ein
            // Server, der hier etwas anderes meldet, spart sich das Lesen.
            val type = connection.contentType
            if (type != null && !type.contains("text/html", ignoreCase = true)) return null

            val body = readLimited(connection) ?: return null
            if (MARKERS.any { !body.contains(it, ignoreCase = true) }) return null

            return BoardInfo(address = address)
        } catch (e: Exception) {
            // Zeitueberschreitung, abgewiesene Verbindung, unlesbare Antwort:
            // alles heisst hier dasselbe, naemlich "kein Geraet an dieser
            // Adresse".
            return null
        } finally {
            connection?.disconnect()
        }
    }

    private fun readLimited(connection: HttpURLConnection): String? {
        connection.inputStream.use { stream ->
            val buffer = ByteArray(MAX_BODY_BYTES)
            var filled = 0
            while (filled < buffer.size) {
                val read = stream.read(buffer, filled, buffer.size - filled)
                if (read < 0) break
                filled += read
            }
            return if (filled == 0) null else String(buffer, 0, filled, Charsets.UTF_8)
        }
    }
}
