package io.github.ft972.patchmanul

/**
 * Ein gefundenes Geraet.
 *
 * Mehr als die Adresse steht hier nicht, und das ist Absicht: Die Firmware
 * meldet ueber HTTP nichts ueber sich selbst - keine Kennung, keine Version,
 * keinen Namen. Sie liefert unter / ihre Weboberflaeche aus und beantwortet
 * jeden anderen Pfad mit 404; alles Weitere laeuft ueber einen WebSocket, den
 * diese App nicht spricht. Erkannt wird ein Geraet deshalb an dieser Seite,
 * siehe [BoardProbe].
 *
 * **Bewusst ohne Versionsangabe:** Es gibt keinen Weg, sie ohne WebSocket zu
 * erfragen. Eine geratene Anzeige waere schlimmer als keine.
 */
data class BoardInfo(
    val address: String
)

/**
 * Ueber welches Merkmal ein Favorit angesprochen wird.
 *
 * Beide haben eine Schwaeche, und zwar eine **gegenlaeufige**: Die IP-Adresse
 * wechselt beim naechsten DHCP-Lease, ueberlebt aber einen Neustart des
 * Geraets. Der DNS-Name ueberlebt umgekehrt den Lease-Wechsel, haengt aber
 * daran, dass der Router ihn kennt und weiterhin auf dasselbe Geraet zeigt.
 *
 * Deshalb wird immer **beides** gespeichert: Was hier steht, ist nur der zuerst
 * probierte Weg. Schlaegt er fehl, greift stillschweigend der andere.
 */
enum class Route { ADDRESS, HOSTNAME }

/**
 * Ein gespeichertes Geraet: der Name, den der Nutzer vergibt, und die beiden
 * Merkmale, ueber die es erreichbar ist.
 *
 * [address] und [hostname] duerfen einzeln leer sein - beide zugleich nicht,
 * sonst gaebe es kein Ziel. Ein von Hand angelegter Favorit kann mit einem
 * blossen Namen anfangen; die Adresse traegt die App beim ersten erfolgreichen
 * Verbinden selbst nach.
 *
 * [routeLocked] merkt sich, ob der Nutzer den Weg selbst festgelegt hat. Ist es
 * false, zieht [route] der Wirklichkeit nach: Was zuletzt geantwortet hat, wird
 * beim naechsten Mal zuerst probiert.
 */
data class Favorite(
    val name: String,
    val address: String,
    val hostname: String = "",
    val route: Route = Route.ADDRESS,
    val routeLocked: Boolean = false
) {

    /**
     * Der Zeilenschluessel fuer den Statusspeicher. Bevorzugt die Adresse, damit
     * ein Favorit und eine Fundstelle desselben Geraets denselben Eintrag teilen
     * und beide Zeilen denselben Stand zeigen.
     */
    val key: String get() = address.ifBlank { hostname }

    /** Das Ziel, das zuerst probiert wird. */
    val preferred: String
        get() = if (route == Route.HOSTNAME && hostname.isNotBlank()) hostname
        else address.ifBlank { hostname }

    /** Der stille Rueckfall - leer, wenn es keinen gibt. */
    val fallback: String
        get() {
            val other = if (preferred == hostname) address else hostname
            return if (other.isBlank() || other == preferred) "" else other
        }
}
