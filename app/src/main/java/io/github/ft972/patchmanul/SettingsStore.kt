package io.github.ft972.patchmanul

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

/**
 * Die Einstellungen: Prueftakt der Favoriten, Zeitgrenze des Suchlaufs,
 * Farbmodus und Sprache.
 *
 * Sprache und Farbmodus werden hier nur zum Anzeigen im Auswahlfeld gehalten -
 * angewendet wird die Sprache ueber AppCompatDelegate.setApplicationLocales(),
 * das sie selbst dauerhaft speichert. Beim Farbmodus gibt es diese Speicherung
 * nicht: AppCompatDelegate.setDefaultNightMode() haelt den Wert nur im
 * Prozessspeicher. PatchManulApplication liest deshalb bei jedem Prozessstart
 * hier nach und wendet ihn an, bevor die erste Activity entsteht.
 */
class SettingsStore(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    /**
     * Abstand zwischen zwei automatischen Erreichbarkeitspruefungen, in
     * Sekunden. **0 heisst aus.**
     */
    var checkIntervalSeconds: Int
        get() = preferences.getInt(KEY_INTERVAL, DEFAULT_INTERVAL)
        set(value) = preferences.edit { putInt(KEY_INTERVAL, value) }

    /**
     * Wie lange der Suchlauf je Adresse auf eine Verbindung wartet, in
     * Millisekunden. Der Lesezeitraum waechst mit - siehe NetworkScanner.
     *
     * Geklemmt beim Lesen, nicht beim Schreiben: Wird die Werteliste spaeter
     * enger gefasst, faellt ein alter Eintrag von selbst wieder hinein.
     */
    var scanTimeoutMs: Int
        get() = preferences.getInt(KEY_SCAN_TIMEOUT, DEFAULT_SCAN_TIMEOUT)
            .coerceIn(SCAN_TIMEOUT_VALUES.first(), SCAN_TIMEOUT_VALUES.last())
        set(value) = preferences.edit { putInt(KEY_SCAN_TIMEOUT, value) }

    /** Einer der AppCompatDelegate.MODE_NIGHT_*-Werte. */
    var themeMode: Int
        get() = preferences.getInt(KEY_THEME, DEFAULT_THEME)
        set(value) = preferences.edit { putInt(KEY_THEME, value) }

    companion object {
        private const val PREFERENCES = "patchmanul"
        private const val KEY_INTERVAL = "check_interval_seconds"
        private const val KEY_SCAN_TIMEOUT = "scan_timeout_ms"
        private const val KEY_THEME = "theme_mode"

        /**
         * Vorgabe: aus. Ein Takt kostet WLAN und Akku, und der Test beim Start
         * und bei jeder Rueckkehr deckt den Normalfall bereits ab.
         */
        const val DEFAULT_INTERVAL = 0

        /** Die Werte des Auswahlfelds, in derselben Reihenfolge wie die Texte. */
        val INTERVAL_VALUES = intArrayOf(0, 15, 30, 60, 300)

        /**
         * Vorgabe **und** Minimum zugleich: der Wert, mit dem der Suchlauf bis
         * 1.4.0 fest gearbeitet hat. Er reicht in einem gewoehnlichen WLAN aus,
         * und er bestimmt die Dauer des Laufs - fuer jede tote Adresse wird er
         * voll abgewartet. Wer nichts einstellt, sucht also genau wie bisher.
         */
        const val DEFAULT_SCAN_TIMEOUT = 600

        /**
         * Die Werte des Auswahlfelds, in Millisekunden. Nach oben endet die
         * Reihe beim **Anderthalbfachen** der Vorgabe: Das ist die Luft fuer
         * traege Netze, in denen ein Board sonst uebersehen wird, und zugleich
         * die Grenze, ab der ein Lauf spuerbar laenger dauert, ohne noch etwas
         * zu finden.
         */
        val SCAN_TIMEOUT_VALUES = intArrayOf(600, 700, 800, 900)

        /** Vorgabe: der Systemmodus - das war schon vor dieser Einstellung das Verhalten. */
        val DEFAULT_THEME = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        /** Die Werte des Auswahlfelds, in derselben Reihenfolge wie die Texte. */
        val THEME_MODES = intArrayOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES
        )

        /** Die Sprachkuerzel; "" steht fuer die Systemsprache. */
        val LANGUAGE_TAGS = arrayOf("", "de", "en", "fr", "es")
    }
}
