package io.github.ft972.patchmanul

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Die Einstellungen: Prueftakt, Zeitgrenze der Suche, Design und Sprache.
 *
 * **Bis 1.4.0 war das ein Dialog.** Im Querformat blieb davon nur ein schmaler
 * Streifen, in dem die unteren Auswahlfelder gar nicht mehr zu sehen waren -
 * ein Dialog waechst nicht ueber die Bildschirmhoehe hinaus und scrollt hier
 * auch nicht. Als eigene Activity mit NestedScrollView ist das erledigt, und
 * der Bildschirm gleicht dem Info-Bildschirm, der aus demselben Grund schon
 * immer eine Activity war.
 *
 * **Mit dem Dialog sind Speichern und Abbrechen entfallen: Jede Wahl gilt
 * sofort.** Das ist nicht nur bequemer, es ist hier auch das einzig
 * verlaessliche Verhalten - Design und Sprache bauen den Bildschirm beim
 * Umschalten neu auf. Ein Speichern-Knopf, der beides anwendet und danach die
 * Seite schliesst, wuerde gegen genau diesen Neuaufbau arbeiten.
 *
 * Der Prueftakt wird hier nur gespeichert, nicht neu gestellt: MainActivity
 * plant ihn in onResume ohnehin neu, und dorthin fuehrt der einzige Weg von
 * dieser Seite zurueck.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var settings: SettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        settings = SettingsStore(this)

        findViewById<View>(R.id.backButton).setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(this) { finish() }

        bindSpinner(
            R.id.intervalSpinner,
            listOf(
                getString(R.string.interval_off),
                getString(R.string.interval_15s),
                getString(R.string.interval_30s),
                getString(R.string.interval_60s),
                getString(R.string.interval_300s)
            ),
            SettingsStore.INTERVAL_VALUES.indexOf(settings.checkIntervalSeconds)
        ) { position ->
            settings.checkIntervalSeconds = SettingsStore.INTERVAL_VALUES[position]
        }

        bindSpinner(
            R.id.scanTimeoutSpinner,
            timeoutLabels(),
            SettingsStore.SCAN_TIMEOUT_VALUES.indexOf(settings.scanTimeoutMs)
        ) { position ->
            settings.scanTimeoutMs = SettingsStore.SCAN_TIMEOUT_VALUES[position]
        }

        bindSpinner(
            R.id.themeSpinner,
            listOf(
                getString(R.string.theme_system),
                getString(R.string.theme_light),
                getString(R.string.theme_dark)
            ),
            SettingsStore.THEME_MODES.indexOf(settings.themeMode)
        ) { position ->
            applyThemeMode(SettingsStore.THEME_MODES[position])
        }

        bindSpinner(
            R.id.languageSpinner,
            listOf(
                getString(R.string.language_system),
                getString(R.string.language_de),
                getString(R.string.language_en),
                getString(R.string.language_fr),
                getString(R.string.language_es)
            ),
            currentLanguageIndex()
        ) { position ->
            applyLanguage(SettingsStore.LANGUAGE_TAGS[position])
        }
    }

    /**
     * Die Randwerte werden benannt, damit klar ist, wofuer sie da sind: Der
     * erste ist der bisherige Festwert, der letzte das Anderthalbfache davon.
     */
    private fun timeoutLabels(): List<String> =
        SettingsStore.SCAN_TIMEOUT_VALUES.mapIndexed { index, value ->
            when (index) {
                0 -> getString(R.string.scan_timeout_default, value)
                SettingsStore.SCAN_TIMEOUT_VALUES.lastIndex ->
                    getString(R.string.scan_timeout_slow, value)
                else -> getString(R.string.scan_timeout_value, value)
            }
        }

    /**
     * Auswahlfeld fuellen, auf den gespeicherten Wert stellen und erst danach
     * zuhoeren.
     *
     * Ein Spinner meldet seine Anfangsstellung beim Aufbau trotzdem noch
     * einmal - dann aber bereits mit [selected]. **Jede Aktion muss deshalb
     * folgenlos bleiben, solange sich nichts aendert.** Speichern desselben
     * Werts ist es ohnehin; Design und Sprache pruefen das ausdruecklich, weil
     * beide sonst den Bildschirm grundlos neu aufbauen wuerden.
     */
    private fun bindSpinner(
        id: Int,
        labels: List<String>,
        selected: Int,
        onChosen: (Int) -> Unit
    ) {
        val spinner = findViewById<Spinner>(id)
        spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, labels).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        spinner.setSelection(selected.coerceAtLeast(0))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) = onChosen(position)

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    /**
     * Wie applyLanguage: nur umschalten, wenn sich wirklich etwas aendert -
     * sonst baute AppCompat die laufende Activity grundlos neu auf.
     */
    private fun applyThemeMode(mode: Int) {
        if (mode == settings.themeMode) return
        settings.themeMode = mode
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun currentLanguageIndex(): Int {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return 0
        val tag = locales.get(0)?.language ?: return 0
        return SettingsStore.LANGUAGE_TAGS.indexOf(tag).coerceAtLeast(0)
    }

    /**
     * Leeres Kuerzel heisst "Systemsprache". AppCompatDelegate speichert die
     * Wahl selbst dauerhaft und baut den Bildschirm neu auf - deshalb wird hier
     * nur umgeschaltet, wenn sich wirklich etwas aendert.
     */
    private fun applyLanguage(tag: String) {
        val wanted =
            if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(tag)

        if (wanted.toLanguageTags() == AppCompatDelegate.getApplicationLocales().toLanguageTags()) {
            return
        }
        AppCompatDelegate.setApplicationLocales(wanted)
    }
}
