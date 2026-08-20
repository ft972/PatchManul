package io.github.ft972.patchmanul

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

/**
 * Die Favoritenliste, als JSON-Text in den SharedPreferences.
 *
 * Bei einer ueberschaubaren Zahl von Geraeten waere eine Datenbank Aufwand ohne
 * Gegenwert.
 *
 * **Es gibt keinen festen Schluessel, und das ist Absicht.** Beide Merkmale
 * eines Eintrags koennen sich aendern: die Adresse beim naechsten DHCP-Lease,
 * der Name, sobald der Router einen anderen eintraegt. Zwei Eintraege gelten
 * deshalb als derselbe, wenn sie sich eines von beiden teilen - siehe
 * [sameEntry].
 *
 * Aeltere Fassungen kannten nur Name und Adresse. Die neuen Felder werden mit
 * optString/optBoolean gelesen und fallen auf ihre Vorgaben zurueck, ein
 * Bestandseintrag landet also im Adressmodus. Nichts geht verloren.
 */
class FavoritesStore(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun list(): List<Favorite> {
        val raw = preferences.getString(KEY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val address = item.optString(FIELD_ADDRESS)
                val hostname = item.optString(FIELD_HOSTNAME)

                // Ohne beides gaebe es kein Ziel - so ein Eintrag ist Schrott.
                if (address.isBlank() && hostname.isBlank()) return@mapNotNull null

                Favorite(
                    name = item.optString(FIELD_NAME),
                    address = address,
                    hostname = hostname,
                    route = if (item.optString(FIELD_ROUTE) == ROUTE_HOSTNAME) Route.HOSTNAME
                    else Route.ADDRESS,
                    routeLocked = item.optBoolean(FIELD_LOCKED, false)
                )
            }
        } catch (e: Exception) {
            // Ein beschaedigter Eintrag darf die App nicht am Start hindern.
            emptyList()
        }
    }

    /** Neu anlegen. Ein Eintrag mit gleichem Merkmal wird dabei ersetzt. */
    fun add(favorite: Favorite) {
        save(list().filterNot { sameEntry(it, favorite) } + favorite)
    }

    /**
     * Einen bestehenden Eintrag ersetzen, **an seiner Stelle in der Liste**.
     * Die Favoriten stehen in der Reihenfolge, in der sie angelegt wurden; ein
     * Namenswechsel oder eine nachgelernte Adresse darf daran nichts aendern.
     */
    fun replace(old: Favorite, new: Favorite) {
        val current = list()
        if (current.none { sameEntry(it, old) }) return
        save(current.map { if (sameEntry(it, old)) new else it })
    }

    fun remove(favorite: Favorite) {
        save(list().filterNot { sameEntry(it, favorite) })
    }

    /** Fuer den "Gemerkt"-Knopf: Steht eines dieser Merkmale schon in der Liste? */
    fun containsAny(targets: List<String>): Boolean {
        val wanted = targets.filter { it.isNotBlank() }.map { it.lowercase() }
        if (wanted.isEmpty()) return false
        return list().any { favorite ->
            favorite.address.lowercase() in wanted || favorite.hostname.lowercase() in wanted
        }
    }

    /**
     * Ob ein Merkmal bereits einem **anderen** Eintrag gehoert.
     *
     * Gebraucht beim Nachlernen: Erfaehrt ein Favorit eine neue Adresse oder
     * einen neuen Namen, darf er ihn einem zweiten Eintrag nicht wegnehmen -
     * sonst zeigten hinterher zwei Zeilen auf dasselbe Geraet.
     */
    fun isTaken(value: String, except: Favorite): Boolean {
        if (value.isBlank()) return false
        return list().any { favorite ->
            !sameEntry(favorite, except) &&
                    (favorite.address.equals(value, ignoreCase = true) ||
                            favorite.hostname.equals(value, ignoreCase = true))
        }
    }

    private fun save(favorites: List<Favorite>) {
        val array = JSONArray()
        for (favorite in favorites) {
            array.put(
                JSONObject()
                    .put(FIELD_NAME, favorite.name)
                    .put(FIELD_ADDRESS, favorite.address)
                    .put(FIELD_HOSTNAME, favorite.hostname)
                    .put(
                        FIELD_ROUTE,
                        if (favorite.route == Route.HOSTNAME) ROUTE_HOSTNAME else ROUTE_ADDRESS
                    )
                    .put(FIELD_LOCKED, favorite.routeLocked)
            )
        }
        preferences.edit { putString(KEY, array.toString()) }
    }

    private companion object {
        const val PREFERENCES = "patchmanul"
        const val KEY = "favorites"

        const val FIELD_NAME = "name"
        const val FIELD_ADDRESS = "address"
        const val FIELD_HOSTNAME = "hostname"
        const val FIELD_ROUTE = "route"
        const val FIELD_LOCKED = "locked"

        const val ROUTE_ADDRESS = "address"
        const val ROUTE_HOSTNAME = "hostname"

        /**
         * Derselbe Eintrag, wenn sich Adresse **oder** Name decken. Leere Felder
         * zaehlen nicht mit: Sonst waeren zwei Eintraege ohne Namen dasselbe.
         */
        fun sameEntry(a: Favorite, b: Favorite): Boolean =
            (a.address.isNotBlank() && a.address.equals(b.address, ignoreCase = true)) ||
                    (a.hostname.isNotBlank() && a.hostname.equals(b.hostname, ignoreCase = true))
    }
}
