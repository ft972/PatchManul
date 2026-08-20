package io.github.ft972.patchmanul

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Der Info-Bildschirm mit den rechtlichen Angaben.
 *
 * **Seit 09.08.2026 ohne Platzhalter.** Was hier steht, stimmt - der
 * Datenschutzabschnitt beschreibt das tatsaechliche Verhalten und ist bei jeder
 * Aenderung daran mitzufuehren.
 *
 * **Mit 1.7.0 auf eine Veroeffentlichung hin durchgesehen** (20.08.2026):
 * - Der Abschnitt **Berechtigungen** kam dazu. Er erklaert alle drei und sagt,
 *   dass keine Standortberechtigung verlangt wird.
 * - Der **Apache-2.0-Lizenztext** liegt jetzt bei und ist ueber einen Knopf zu
 *   sehen ([LicenseActivity]) - die Lizenz verlangt das bei Weitergabe.
 * - Die Zusage, dass die Favoriten das Geraet nicht verlassen, stimmt seither
 *   woertlich: Das Backup ist im Manifest abgeschaltet.
 *
 * **Impressumsangaben fehlen weiterhin** - Kontakt, Verantwortlicher,
 * Streitbeilegung. Ob und wann sie noetig sind, ist eine Rechtsfrage und von
 * niemandem mit Sachkenntnis geprueft.
 *
 * Eine eigene Activity statt eines Dialogs, weil der Inhalt zu lang dafuer ist
 * und gescrollt werden muss.
 */
class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        findViewById<TextView>(R.id.versionText).text = getString(R.string.info_version, version())
        findViewById<View>(R.id.backButton).setOnClickListener { finish() }
        findViewById<View>(R.id.licenseButton).setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }
        onBackPressedDispatcher.addCallback(this) { finish() }
    }

    /**
     * Aus dem Paketmanager statt aus `BuildConfig` - so bleibt die Erzeugung
     * der BuildConfig-Klasse abgeschaltet, die AGP seit Version 8 nicht mehr
     * von sich aus anlegt.
     */
    private fun version(): String {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "?"
        } catch (e: Exception) {
            "?"
        }
    }
}
