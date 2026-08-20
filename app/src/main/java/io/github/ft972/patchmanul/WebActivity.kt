package io.github.ft972.patchmanul

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Zeigt die Weboberflaeche eines Geraets. Sie liegt unter http://<adresse>/ auf
 * Port 80 - denselben Pfad, an dem auch der Suchlauf das Geraet erkennt.
 *
 * **Mehr als anzeigen tut dieser Bildschirm nicht.** Was die Seite kann, kann
 * sie selbst; die App greift ihr nicht hinein. Es gibt hier deshalb keinen
 * eigenen Prueftakt und keine zweite Seite, zwischen denen umzuschalten waere -
 * die Firmware liefert unter / alles aus und beantwortet jeden anderen Pfad mit
 * 404.
 */
class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var errorView: TextView
    private lateinit var urlView: TextView

    private lateinit var mainUrl: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val address = intent.getStringExtra(EXTRA_ADDRESS)
        if (address.isNullOrBlank()) {
            finish()
            return
        }
        val name = intent.getStringExtra(EXTRA_NAME)?.takeIf { it.isNotBlank() } ?: address
        mainUrl = "http://$address/"

        findViewById<TextView>(R.id.webTitle).text = name
        urlView = findViewById(R.id.webUrl)
        urlView.text = mainUrl

        progress = findViewById(R.id.webProgress)
        errorView = findViewById(R.id.webError)
        webView = findViewById(R.id.webView)

        findViewById<View>(R.id.reloadButton).setOnClickListener {
            errorView.visibility = View.GONE
            webView.reload()
        }

        // Anders als die Zurueck-Taste: kein Blaettern durch die Seite, sondern
        // direkt zurueck zur Geraeteliste. MainActivity steht schon unter dieser
        // Activity im Stapel - finish() legt sie frei, ohne einen neuen Intent.
        findViewById<View>(R.id.homeButton).setOnClickListener { goHome() }

        with(webView.settings) {
            // Die Oberflaeche baut ihren Inhalt per JavaScript auf und legt
            // Zustand im DOM-Storage ab; ohne beides bleibt sie leer.
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            // Ein Geraete-Dashboard soll den Ist-Zustand zeigen, nicht den von
            // vorhin. Die Seite ist klein genug, dass das nichts kostet.
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                errorView.visibility = View.GONE
                progress.visibility = View.VISIBLE
                showPage(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progress.visibility = View.GONE
                showPage(url)
            }

            /**
             * Faengt die Wege ab, bei denen onPageStarted nicht feuert: das
             * Zurueckblaettern und die History-Aufrufe der Geraeteseite. Ohne
             * das stuende in der Adresszeile noch die vorige Ansicht.
             */
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                showPage(url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // Nur der Hauptrahmen zaehlt - ein fehlendes Favicon ist kein
                // Grund, dem Nutzer eine Fehlermeldung hinzustellen.
                if (request?.isForMainFrame != true) return
                progress.visibility = View.GONE
                errorView.visibility = View.VISIBLE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
            }
        }

        onBackPressedDispatcher.addCallback(this) { goBack() }

        webView.loadUrl(mainUrl)
    }

    /** Die Adresszeile folgt dem, was tatsaechlich zu sehen ist. */
    private fun showPage(url: String?) {
        if (url.isNullOrBlank() || url == "about:blank") return
        urlView.text = url
    }

    /**
     * Fuer die Zurueck-Taste des Systems: blaettert erst durch die Seite und
     * verlaesst den Bildschirm erst, wenn es dort nichts mehr zurueckzugehen
     * gibt. Der Home-Knopf oben links geht dagegen immer direkt zurueck - siehe
     * goHome().
     */
    private fun goBack() {
        if (webView.canGoBack()) webView.goBack() else finish()
    }

    private fun goHome() {
        finish()
    }

    override fun onDestroy() {
        // Bei fehlender Adresse bricht onCreate vor der Zuweisung ab.
        if (::webView.isInitialized) webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_ADDRESS = "address"
        const val EXTRA_NAME = "name"
    }
}
