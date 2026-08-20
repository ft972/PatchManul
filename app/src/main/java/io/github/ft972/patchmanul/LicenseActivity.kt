package io.github.ft972.patchmanul

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Zeigt den vollstaendigen Apache-2.0-Lizenztext.
 *
 * **Warum es diesen Bildschirm gibt:** Apache 2.0 verlangt in Abschnitt 4,
 * dass bei einer Weitergabe eine Kopie der Lizenz beiliegt. Der Info-Bildschirm
 * nannte bis 1.6.2 nur ihren Namen - das genuegte fuer den Eigengebrauch, nicht
 * fuer eine Veroeffentlichung.
 *
 * Der Text steht als `res/raw/apache_2_0.txt` im Projekt und stammt aus den
 * Bibliotheken selbst: AndroidX liefert ihn unter `META-INF` mit, und mehrere
 * von ihnen tun das byte-identisch. Damit ist es nicht irgendeine Abschrift,
 * sondern genau die Kopie, die zu den eingebundenen Bibliotheken gehoert.
 *
 * **Der Text wird nicht uebersetzt.** Massgeblich ist die englische Fassung;
 * eine Uebersetzung waere nicht die Lizenz, sondern eine Auslegung davon.
 *
 * Eine eigene Activity aus demselben Grund wie beim Info-Bildschirm: 178 Zeilen
 * passen in keinen Dialog.
 */
class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_license)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        findViewById<TextView>(R.id.licenseText).text = licenseText()
        findViewById<View>(R.id.backButton).setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(this) { finish() }
    }

    /**
     * Die Datei liegt im APK und ist rund 10 KB gross - sie in einem Zug zu
     * lesen ist unbedenklich. Schlaegt es wider Erwarten fehl, bleibt der
     * Bildschirm lieber leer, als dass die App abstuerzt; der Lizenztext liegt
     * dem Quelltext ohnehin auch als Datei bei.
     */
    private fun licenseText(): String {
        return try {
            resources.openRawResource(R.raw.apache_2_0).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }
}
