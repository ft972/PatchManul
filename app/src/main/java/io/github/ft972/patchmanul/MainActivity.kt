package io.github.ft972.patchmanul

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

/**
 * Die Startseite: oben das Ergebnis des letzten Suchlaufs, unten die
 * gespeicherten Geraete mit ihrer Statusmarkierung.
 *
 * Beide Listen werden bei jeder Aenderung neu aufgebaut. Bei einer ueberschau-
 * baren Zahl von Eintraegen ist das billiger als ein RecyclerView samt Adapter -
 * und deutlich weniger Code.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var favorites: FavoritesStore
    private lateinit var settings: SettingsStore
    private lateinit var scanner: NetworkScanner
    private val reachability = ReachabilityCheck()
    private val hostnameLookup = HostnameLookup()

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Der wiederkehrende Erreichbarkeitstest. Laeuft nur, solange die App im
     * Vordergrund ist - ein Hintergrunddienst waere fuer diesen Zweck
     * unverhaeltnismaessig und ginge auf den Akku.
     */
    private val periodicCheck = object : Runnable {
        override fun run() {
            // Ohne Zuruecksetzen: Beim Nachpruefen sollen die Zeilen nicht
            // jedes Mal kurz auf grau springen.
            checkDevices(resetStatus = false)
            scheduleNextCheck()
        }
    }

    private lateinit var favoritesContainer: LinearLayout
    private lateinit var favoritesEmpty: TextView
    private lateinit var scanContainer: LinearLayout
    private lateinit var scanStatus: TextView
    private lateinit var scanProgress: ProgressBar
    private lateinit var scanButton: MaterialButton

    /** Die Boards des letzten Suchlaufs, aufsteigend nach Adresse. */
    private val found = mutableListOf<BoardInfo>()

    /**
     * Was der letzte Erreichbarkeitstest je Zeile ergeben hat. Fehlt der
     * Eintrag, laeuft die Pruefung noch (graue Markierung); null heisst "nicht
     * erreichbar" (rot), sonst gruen.
     *
     * Zusaetzlich zum Zeilenschluessel steht das Ergebnis auch unter der
     * bestaetigten IP - so teilen ein ueber den Namen gefuehrter Favorit und
     * die Fundstelle desselben Geraets denselben Stand.
     */
    private val status = mutableMapOf<String, BoardInfo?>()

    /**
     * Welches Ziel je Zeile zuletzt tatsaechlich geantwortet hat. Damit steht
     * in der zweiten Zeile das, was traegt - und der eingesprungene Rueckfall
     * bleibt sichtbar statt sich zu verstecken.
     */
    private val reachedVia = mutableMapOf<String, String>()

    /**
     * Die Namen, die der DNS-Server zu den gefundenen Adressen kennt, samt
     * Gegenprobe. Fehlt ein Eintrag, gibt es dort keinen - dann bleibt es bei
     * der Adresse.
     */
    private val hostnames = mutableMapOf<String, HostnameInfo>()

    private val localNetworkPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        favorites = FavoritesStore(this)
        settings = SettingsStore(this)
        scanner = NetworkScanner(this)

        favoritesContainer = findViewById(R.id.favoritesContainer)
        favoritesEmpty = findViewById(R.id.favoritesEmpty)
        scanContainer = findViewById(R.id.scanContainer)
        scanStatus = findViewById(R.id.scanStatus)
        scanProgress = findViewById(R.id.scanProgress)
        scanButton = findViewById(R.id.scanButton)

        scanButton.setOnClickListener { startScan() }
        findViewById<MaterialButton>(R.id.addFavoriteButton).setOnClickListener {
            showFavoriteDialog()
        }
        findViewById<View>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<View>(R.id.infoButton).setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        requestLocalNetworkPermission()
        renderFavorites()
    }

    /**
     * Der Erreichbarkeitstest laeuft beim Start und bei jeder Rueckkehr - also
     * auch, wenn man aus der Weboberflaeche zurueckkommt oder das Geraet
     * zwischendurch neu gestartet wurde.
     */
    override fun onResume() {
        super.onResume()
        checkDevices()
        scheduleNextCheck()
    }

    override fun onPause() {
        // Im Hintergrund wird nicht weitergeprueft.
        handler.removeCallbacks(periodicCheck)
        super.onPause()
    }

    override fun onDestroy() {
        handler.removeCallbacks(periodicCheck)
        scanner.cancel()
        reachability.cancel()
        hostnameLookup.cancel()
        super.onDestroy()
    }

    private fun scheduleNextCheck() {
        handler.removeCallbacks(periodicCheck)
        val seconds = settings.checkIntervalSeconds
        if (seconds > 0) handler.postDelayed(periodicCheck, seconds * 1000L)
    }

    // ── Erreichbarkeit ─────────────────────────────────────────────────────

    /**
     * Prueft alles, was gerade angezeigt wird: die Favoriten **und** die
     * Fundstellen des letzten Suchlaufs.
     *
     * Beide Listen fuehren ihre Adressen im selben Statusspeicher. Ein Geraet,
     * das in beiden steht, wird deshalb nur einmal gefragt - und beide Zeilen
     * zeigen danach denselben Stand.
     *
     * [resetStatus] steuert, ob die Markierungen vorher auf "wird geprueft"
     * zurueckfallen. Beim ersten Lauf ist das richtig; beim wiederkehrenden
     * Nachpruefen wuerde die Liste sonst im Takt flackern.
     */
    private fun checkDevices(resetStatus: Boolean = true) {
        val targets = mutableListOf<ReachabilityCheck.Target>()
        val seen = mutableSetOf<String>()

        for (favorite in favorites.list()) {
            if (seen.add(favorite.key.lowercase())) {
                targets += ReachabilityCheck.Target(
                    key = favorite.key,
                    preferred = favorite.preferred,
                    fallback = favorite.fallback
                )
            }
        }
        for (board in found) {
            // Eine Fundstelle wurde soeben unter dieser Adresse gefunden - sie
            // braucht weder einen Namen noch einen Rueckfall.
            if (seen.add(board.address.lowercase())) {
                targets += ReachabilityCheck.Target(board.address, board.address, "")
            }
        }

        if (resetStatus) status.clear()
        renderFavorites()
        renderFound()

        reachability.run(
            targets = targets,
            onResult = { target, result ->
                status[target.key] = result.board
                if (result.address.isNotBlank()) status[result.address] = result.board
                reachedVia[target.key] = result.reached
                learn(target, result)
                renderFavorites()
                renderFound()
            },
            onFinished = { }
        )
    }

    /**
     * Was die Pruefung nebenbei ueber einen Favoriten verraten hat, zurueck in
     * den Speicher: die bestaetigte Adresse und - solange der Weg nicht
     * festgelegt ist - der Weg, der tatsaechlich getragen hat.
     *
     * **Geschrieben wird nur, wenn sich wirklich etwas aendert.** Sonst liefe
     * bei eingeschaltetem Prueftakt alle paar Sekunden ein Schreibvorgang.
     *
     * Der Name wird hier **nicht** nachgelernt: Er kaeme aus einer PTR-Abfrage,
     * und die kostet einen eigenen Netzumlauf je Favorit. Dafuer ist der
     * Suchlauf da, wo sie ohnehin laeuft.
     */
    private fun learn(target: ReachabilityCheck.Target, result: ReachabilityCheck.Result) {
        if (result.board == null) return
        val favorite = favorites.list().firstOrNull { it.key == target.key } ?: return
        var updated = favorite

        val byName = favorite.hostname.isNotBlank() &&
                result.reached.equals(favorite.hostname, ignoreCase = true)

        // Ueber den Namen erreicht: Damit steht die IP dahinter fest.
        if (byName && !result.address.equals(favorite.address, ignoreCase = true) &&
            !favorites.isTaken(result.address, favorite)
        ) {
            updated = updated.copy(address = result.address)
        }

        if (!favorite.routeLocked) {
            val route = if (byName) Route.HOSTNAME else Route.ADDRESS
            if (route != favorite.route) updated = updated.copy(route = route)
        }

        if (updated != favorite) favorites.replace(favorite, updated)
    }

    private fun renderFavorites() {
        val list = favorites.list()
        favoritesEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        favoritesContainer.removeAllViews()

        for (favorite in list) {
            val row = layoutInflater.inflate(R.layout.item_device, favoritesContainer, false)
            val dot = row.findViewById<TextView>(R.id.statusDot)
            val subtitle = row.findViewById<TextView>(R.id.deviceSubtitle)
            val menu = row.findViewById<View>(R.id.menuButton)

            // Das Ziel, das zuletzt geantwortet hat - sonst das, was als
            // Naechstes probiert wird.
            val target = reachedVia[favorite.key]?.ifBlank { null } ?: favorite.preferred
            val shown = shortTarget(target)

            row.findViewById<TextView>(R.id.deviceName).text = favorite.name.ifBlank { shown }

            val checked = status.containsKey(favorite.key)
            val board = status[favorite.key]
            val state = when {
                !checked -> {
                    dot.setTextColor(color(R.color.status_unknown))
                    getString(R.string.status_checking)
                }

                board != null -> {
                    dot.setTextColor(color(R.color.status_online))
                    getString(R.string.status_online)
                }

                else -> {
                    dot.setTextColor(color(R.color.status_offline))
                    getString(R.string.status_offline)
                }
            }

            // Ohne eigenen Namen steht das Ziel schon in der ersten Zeile.
            // Es hier zu wiederholen, fuellt nur Platz.
            val named = favorite.name.isNotBlank() && favorite.name != shown
            subtitle.text = if (named) detail(shown, state) else state

            menu.visibility = View.VISIBLE
            menu.setOnClickListener { showFavoriteMenu(menu, favorite) }
            row.setOnClickListener { openDevice(favorite.name, favorite.key, target) }

            favoritesContainer.addView(row)
        }
    }

    /**
     * Gilt fuer beide Listen - die Rueckfrage haengt allein am Status.
     *
     * [key] ist der Schluessel der Zeile im Statusspeicher, [target] das Ziel,
     * das geoeffnet werden soll: die Adresse oder der Name, je nachdem, was
     * zuletzt getragen hat.
     */
    private fun openDevice(name: String, key: String, target: String) {
        // Nur wenn die Pruefung schon durch ist und nichts geantwortet hat,
        // wird nachgefragt. Sonst landet man auf einer weissen Fehlerseite und
        // weiss nicht, warum.
        //
        // **Kein "Trotzdem oeffnen".** Wer nicht antwortet, hat auch keine Seite
        // zu zeigen - der Knopf fuehrte nur auf eine Fehlermeldung der WebView.
        // Angeboten wird deshalb der Weg, der etwas aendern kann: neu pruefen.
        if (status.containsKey(key) && status[key] == null) {
            AlertDialog.Builder(this)
                .setTitle(R.string.offline_title)
                .setMessage(getString(R.string.offline_message, shortTarget(target)))
                .setPositiveButton(R.string.offline_recheck) { _, _ -> checkDevices() }
                .setNegativeButton(R.string.dialog_cancel, null)
                .show()
            return
        }
        open(name, target)
    }

    /** Das Drei-Punkte-Menü einer Favoritenzeile: Bearbeiten, Entfernen. */
    private fun showFavoriteMenu(anchor: View, favorite: Favorite) {
        PopupMenu(this, anchor).apply {
            menuInflater.inflate(R.menu.favorite_actions, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        showFavoriteDialog(favorite, favorite)
                        true
                    }

                    R.id.action_remove -> {
                        confirmRemove(favorite)
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }

    /**
     * Das Drei-Punkte-Menü einer Suchzeile. Darin steht nur noch **Merken** -
     * und die Zeile bietet es gar nicht erst an, wenn das Gerät schon gemerkt
     * ist (siehe renderFound). Ein Menü für einen einzigen Punkt ist trotzdem
     * das Richtige: Beide Listen werden gleich bedient, und ein Knopf in der
     * Zeile bräuchte Platz, den sie nicht hat.
     */
    private fun showScanMenu(anchor: View, board: BoardInfo) {
        PopupMenu(this, anchor).apply {
            menuInflater.inflate(R.menu.scan_actions, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_keep -> {
                        showFavoriteDialog(draftFor(board))
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }


    private fun confirmRemove(favorite: Favorite) {
        AlertDialog.Builder(this)
            .setTitle(R.string.remove_title)
            .setMessage(
                getString(R.string.remove_message, favorite.name.ifBlank { shortTarget(favorite.preferred) })
            )
            .setPositiveButton(R.string.remove) { _, _ ->
                favorites.remove(favorite)
                status.remove(favorite.key)
                reachedVia.remove(favorite.key)
                renderFavorites()
                // Der "Merken"-Knopf in der Suchliste wird wieder benutzbar.
                renderFound()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    /**
     * Der Dialog fuer neue und fuer bestehende Favoriten.
     *
     * [draft] belegt die Felder vor - beim Uebernehmen aus der Suche mit dem
     * Vorschlag, beim Bearbeiten mit dem bestehenden Eintrag. [replacing] ist
     * nur im zweiten Fall gesetzt: Dann muss der alte Eintrag weichen, denn
     * beide Merkmale koennen sich geaendert haben.
     */
    private fun showFavoriteDialog(draft: Favorite? = null, replacing: Favorite? = null) {
        val view = layoutInflater.inflate(R.layout.dialog_favorite, null)
        val nameInput = view.findViewById<TextInputEditText>(R.id.nameInput)
        val hostnameInput = view.findViewById<TextInputEditText>(R.id.hostnameInput)
        val hostnameLayout = view.findViewById<TextInputLayout>(R.id.hostnameLayout)
        val addressLabel = view.findViewById<TextView>(R.id.addressLabel)
        val addressRow = view.findViewById<View>(R.id.addressRow)
        val addressError = view.findViewById<TextView>(R.id.addressError)
        val hostnameError = view.findViewById<TextView>(R.id.hostnameError)
        val toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.routeToggle)
        val routeHint = view.findViewById<TextView>(R.id.routeHint)
        val parts = listOf(R.id.ip1, R.id.ip2, R.id.ip3, R.id.ip4)
            .map { view.findViewById<EditText>(it) }

        // Die Fehlermeldung muss verschwinden, sobald jemand die Eingabe
        // korrigiert. Sie erst beim naechsten Speichern-Klick zu loeschen, liesse
        // eine rote Meldung ueber einer laengst richtigen Eingabe stehen.
        wireAddressMask(parts) { addressError.visibility = View.GONE }
        hostnameInput.doOnTextChanged { _, _, _, _ -> hostnameError.visibility = View.GONE }

        draft?.let { given ->
            nameInput.setText(given.name)
            hostnameInput.setText(given.hostname)
            given.address.split('.').let { values ->
                if (values.size == parts.size) {
                    parts.forEachIndexed { index, field -> field.setText(values[index]) }
                }
            }
        }

        var route = draft?.route ?: Route.ADDRESS
        var locked = draft?.routeLocked ?: false

        // Der nicht gewaehlte Weg wird **ausgegraut, nicht entfernt**: Er bleibt
        // als stiller Rueckfall in Gebrauch, und sein Wert traegt die App selbst
        // nach. Wegzunehmen, was das Sicherheitsnetz spannt, waere ein
        // schlechter Tausch.
        fun applyRoute() {
            val byName = route == Route.HOSTNAME
            hostnameLayout.isEnabled = byName
            hostnameInput.isEnabled = byName
            parts.forEach { it.isEnabled = !byName }
            addressLabel.alpha = if (byName) DIMMED else 1f
            addressRow.alpha = if (byName) DIMMED else 1f
            routeHint.setText(if (locked) R.string.route_hint_locked else R.string.route_hint_auto)
        }

        // Erst setzen, dann horchen - sonst zaehlte die Vorbelegung schon als
        // Eingriff des Nutzers und legte den Weg fest.
        toggle.check(if (route == Route.HOSTNAME) R.id.routeHostname else R.id.routeAddress)
        toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            route = if (checkedId == R.id.routeHostname) Route.HOSTNAME else Route.ADDRESS
            // Wer den Schalter anfasst, legt den Weg fest - ab da laesst die
            // Automatik den Eintrag in Ruhe.
            locked = true
            addressError.visibility = View.GONE
            hostnameError.visibility = View.GONE
            applyRoute()
        }
        applyRoute()

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                if (replacing == null) R.string.dialog_favorite_title
                else R.string.dialog_favorite_edit_title
            )
            .setView(view)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()

        // Der Standardknopf schliesst den Dialog sofort. Damit eine Fehleingabe
        // nicht alles Getippte verwirft, wird der Klick erst nach dem Anzeigen
        // ueberschrieben - dann bleibt der Dialog bei einem Fehler stehen.
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val values = parts.map { it.text.toString().trim() }
                val name = nameInput.text?.toString()?.trim().orEmpty()
                val hostname = hostnameInput.text?.toString()?.trim().orEmpty().trimEnd('.')

                // Die Adresse ist nur im Adressmodus Pflicht. Im Namensmodus
                // darf sie fehlen - dann traegt die App sie beim ersten
                // erfolgreichen Verbinden selbst nach. Halb ausgefuellt ist
                // dagegen in beiden Faellen ein Fehler.
                val addressGiven = values.any { it.isNotEmpty() }
                val addressProblem = when {
                    !addressGiven ->
                        if (route == Route.ADDRESS) R.string.error_address_empty else null

                    values.any { it.isEmpty() } ->
                        if (route == Route.ADDRESS) R.string.error_address_empty
                        else R.string.error_address_incomplete

                    values.any { !isAddressPart(it) } -> R.string.error_address_format
                    else -> null
                }

                val hostnameProblem = when {
                    hostname.isEmpty() ->
                        if (route == Route.HOSTNAME) R.string.error_hostname_empty else null

                    hostname.endsWith(MDNS_SUFFIX, ignoreCase = true) -> R.string.error_hostname_mdns
                    !isHostname(hostname) -> R.string.error_hostname_format
                    else -> null
                }

                if (addressProblem != null) {
                    addressError.setText(addressProblem)
                    addressError.visibility = View.VISIBLE
                    parts.firstOrNull { it.text.isEmpty() || !isAddressPart(it.text.toString()) }
                        ?.requestFocus()
                }
                if (hostnameProblem != null) {
                    hostnameError.setText(hostnameProblem)
                    hostnameError.visibility = View.VISIBLE
                    if (addressProblem == null) hostnameInput.requestFocus()
                }
                if (addressProblem != null || hostnameProblem != null) return@setOnClickListener

                val address = if (addressGiven) values.joinToString(".") else ""
                addressError.visibility = View.GONE
                hostnameError.visibility = View.GONE

                // Beim Bearbeiten kann sich jedes der beiden Merkmale geaendert
                // haben. Der alte Eintrag muss deshalb weichen, sonst stuenden
                // hinterher zwei da.
                if (replacing != null) {
                    favorites.remove(replacing)
                    status.remove(replacing.key)
                    reachedVia.remove(replacing.key)
                }
                favorites.add(
                    Favorite(
                        name = name.ifBlank { address.ifBlank { shortTarget(hostname) } },
                        address = address,
                        hostname = hostname,
                        route = route,
                        routeLocked = locked
                    )
                )

                // Die Gegenprobe zum Namen: Sie **warnt, sie lehnt nicht ab**.
                // Ein Name kann gerade deshalb nicht aufloesen, weil das Geraet
                // aus ist - das waere ein schlechter Grund, die Eingabe zu
                // verwerfen.
                if (route == Route.HOSTNAME) {
                    hostnameLookup.verify(hostname) { resolved ->
                        if (!resolved) {
                            Toast.makeText(
                                this,
                                R.string.warn_hostname_unresolved,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                dialog.dismiss()
                renderFound()
                checkDevices()
            }
        }
        dialog.show()
    }

    // ── Suche ──────────────────────────────────────────────────────────────

    /**
     * Ist das WLAN groesser als /24 geschnitten, wird vorher gefragt: Ein
     * vollstaendiger Lauf kann dort Minuten dauern, und ohne Vorwarnung sieht
     * das aus, als haenge die App.
     */
    private fun startScan() {
        if (scanner.isRunning) return

        val survey = scanner.survey()
        if (survey == null || !survey.needsChoice) {
            runScan(fullRange = false)
            return
        }

        if (survey.fullHostCount == 0) {
            // Zu gross, um es ueberhaupt anzubieten - dann nur der Hinweis,
            // dass die Suche nicht alles abdeckt.
            AlertDialog.Builder(this)
                .setTitle(R.string.subnet_warning_title)
                .setMessage(
                    getString(
                        R.string.subnet_too_large_message,
                        survey.prefixLength,
                        count(survey.narrowedHostCount)
                    )
                )
                .setPositiveButton(R.string.subnet_warning_narrow) { _, _ -> runScan(false) }
                .setNegativeButton(R.string.dialog_cancel, null)
                .show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.subnet_warning_title)
            .setMessage(
                getString(
                    R.string.subnet_warning_message,
                    survey.prefixLength,
                    count(survey.fullHostCount),
                    duration(scanner.estimatedSeconds(survey.fullHostCount)),
                    count(survey.narrowedHostCount)
                )
            )
            .setPositiveButton(R.string.subnet_warning_narrow) { _, _ -> runScan(false) }
            .setNeutralButton(R.string.subnet_warning_full) { _, _ -> runScan(true) }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    private fun runScan(fullRange: Boolean) {
        found.clear()
        hostnames.clear()
        renderFound()

        scanProgress.isIndeterminate = false
        scanProgress.progress = 0
        scanProgress.visibility = View.VISIBLE
        scanButton.isEnabled = false
        scanStatus.setText(R.string.scan_running)

        scanner.start(object : NetworkScanner.Listener {
            override fun onBoardFound(board: BoardInfo) {
                found.add(board)
                found.sortBy { addressKey(it.address) }
                // Der Fund ist selbst eine Erreichbarkeitspruefung. Ihn gleich
                // einzutragen haelt beide Listen im Gleichklang: Ein Favorit
                // unter derselben Adresse wird damit sofort gruen.
                status[board.address] = board
                renderFound()
                renderFavorites()
            }

            override fun onProgress(done: Int, total: Int) {
                scanProgress.max = total
                scanProgress.progress = done
                // Den Text nicht bei jeder einzelnen Adresse neu setzen: Bei
                // einem grossen Netz waeren das Zehntausende Aktualisierungen.
                if (done == total || done % PROGRESS_STEP == 0) {
                    scanStatus.text = getString(R.string.scan_progress, count(done), count(total))
                }
            }

            override fun onFinished(scanned: Int, subnet: String?) {
                scanProgress.visibility = View.GONE
                scanButton.isEnabled = true
                scanStatus.text = if (subnet == null) {
                    getString(R.string.scan_no_network)
                } else {
                    getString(R.string.scan_result, count(scanned), subnet, found.size)
                }

                // Erst jetzt nach Namen fragen: Waehrend des Sweeps stuende
                // nicht fest, welche Adressen ueberhaupt Geraete sind, und
                // Hunderte PTR-Abfragen waeren reine Last.
                hostnameLookup.run(found.map { it.address }) { address, info ->
                    if (info != null) {
                        hostnames[address] = info
                        dropAmbiguousNames()
                        adoptHostname(address, info.fqdn)
                        renderFound()
                        renderFavorites()
                    }
                }
            }
        }, fullRange)
    }

    private fun renderFound() {
        scanContainer.removeAllViews()

        for (board in found) {
            val row = layoutInflater.inflate(R.layout.item_device, scanContainer, false)
            val menu = row.findViewById<View>(R.id.menuButton)

            val dot = row.findViewById<TextView>(R.id.statusDot)

            // Kennt der DNS-Server einen Namen, steht der oben und die Adresse
            // rueckt in die zweite Zeile - so wie bei den Favoriten auch.
            val hostname = hostnames[board.address]
            val name = hostname?.label ?: board.address
            row.findViewById<TextView>(R.id.deviceName).text = name

            // Beim Fund war das Geraet erreichbar; danach entscheidet die
            // laufende Pruefung - eine Fundstelle kann also rot werden, wenn das
            // Geraet inzwischen weg ist.
            val checked = status.containsKey(board.address)
            val live = status[board.address]
            val state = when {
                !checked || live != null -> {
                    dot.setTextColor(color(R.color.status_online))
                    getString(R.string.status_online)
                }

                else -> {
                    dot.setTextColor(color(R.color.status_offline))
                    getString(R.string.status_offline)
                }
            }
            // Ein namensgefuehrter Favorit steht unter demselben Geraet - ohne
            // den zweiten Vergleich hiesse es hier "Merken" und man legte einen
            // zweiten Eintrag an.
            val known = favorites.containsAny(listOfNotNull(board.address, hostname?.fqdn))

            // "gemerkt" steht in der Zeile statt auf einem Knopf: Das Merken
            // sitzt im Menue, und ohne diesen Hinweis muesste man es erst
            // aufklappen, um zu sehen, was schon gemerkt ist.
            val line = if (hostname != null) detail(board.address, state) else state
            row.findViewById<TextView>(R.id.deviceSubtitle).text =
                if (known) detail(line, getString(R.string.kept)) else line

            // Ist das Geraet schon gemerkt, bliebe das Menue leer - dann gar
            // nicht erst eines anbieten. Dass es gemerkt ist, steht ohnehin in
            // der Zeile.
            menu.visibility = if (known) View.GONE else View.VISIBLE
            menu.setOnClickListener { showScanMenu(menu, board) }
            row.setOnClickListener { openDevice(name, board.address, board.address) }

            scanContainer.addView(row)
        }
    }

    // ── Kleinkram ──────────────────────────────────────────────────────────

    private fun open(name: String, target: String) {
        startActivity(
            Intent(this, WebActivity::class.java)
                .putExtra(WebActivity.EXTRA_NAME, name.ifBlank { shortTarget(target) })
                // Ein Name taugt hier genauso wie eine Adresse: Die WebView loest
                // ihn selbst auf.
                .putExtra(WebActivity.EXTRA_ADDRESS, target)
        )
    }

    private fun requestLocalNetworkPermission() {
        if (Build.VERSION.SDK_INT < API_LOCAL_NETWORK) return
        val granted = ContextCompat.checkSelfPermission(this, PERMISSION_LOCAL_NETWORK) ==
                PackageManager.PERMISSION_GRANTED
        if (!granted) localNetworkPermission.launch(PERMISSION_LOCAL_NETWORK)
    }

    /**
     * Der Vorschlag beim Uebernehmen aus der Suche.
     *
     * Der Anzeigename kommt bevorzugt vom DNS-Server, sonst greift
     * "Geraet <letztes Adressglied>". Den **Weg** entscheidet die Gegenprobe:
     * Nur ein Name, der auch vorwaerts auf dieselbe Adresse zeigt, wird
     * vorbelegt - denn manche Router beantworten die eine Richtung und die
     * andere nicht. Mitgespeichert wird er trotzdem, als Rueckfall.
     */
    private fun draftFor(board: BoardInfo): Favorite {
        val info = hostnames[board.address]
        return Favorite(
            name = info?.label
                ?: getString(R.string.name_suggestion, board.address.substringAfterLast('.')),
            address = board.address,
            hostname = info?.fqdn.orEmpty(),
            route = if (info?.verified == true) Route.HOSTNAME else Route.ADDRESS
        )
    }

    /** Zahlen mit Tausendertrennung in der eingestellten Sprache. */
    private fun count(value: Int): String = NumberFormat.getIntegerInstance().format(value)

    /**
     * Sekunden und Minuten als plurals, damit nicht "1 Sekunden" dasteht.
     * Bei Stunden steht eine Nachkommastelle davor, da greift keine Mehrzahlform.
     */
    private fun duration(seconds: Int): String = when {
        seconds < 90 ->
            resources.getQuantityString(R.plurals.duration_seconds, seconds, seconds)

        seconds < 5400 -> {
            val minutes = (seconds + 30) / 60
            resources.getQuantityString(R.plurals.duration_minutes, minutes, minutes)
        }

        else -> getString(
            R.string.duration_hours,
            String.format(Locale.getDefault(), "%.1f", seconds / 3600.0)
        )
    }

    private fun color(id: Int): Int = ContextCompat.getColor(this, id)

    private fun detail(left: String, right: String): String = "$left · $right"

    /**
     * Adressen bleiben stehen, Namen werden auf ihr erstes Glied gekuerzt: Die
     * Domaene gehoert zum Verbinden, in der Zeile fuellt sie nur Platz.
     */
    private fun shortTarget(target: String): String =
        if (target.any { it.isLetter() }) target.substringBefore('.') else target

    /**
     * Zeigen zwei Fundstellen desselben Laufs auf denselben Namen, taugt er fuer
     * keine von beiden als Merkmal - beim naechsten Mal koennte er das andere
     * Geraet treffen.
     *
     * Der Fall tritt ein, solange die Firmware dem Router keinen eigenen
     * Hostnamen meldet: Dann traegt jedes Geraet den Vorgabewert der ESP-IDF,
     * und der ist auf allen derselbe. Bewusst ohne fest eingetragene
     * Zeichenkette - die Regel greift fuer jeden doppelten Namen.
     */
    private fun dropAmbiguousNames() {
        val counts = hostnames.values.groupingBy { it.fqdn.lowercase() }.eachCount()
        for ((address, info) in hostnames.toList()) {
            if (info.verified && (counts[info.fqdn.lowercase()] ?: 0) > 1) {
                hostnames[address] = info.copy(verified = false)
            }
        }
    }

    /**
     * Ein Favorit unter dieser Adresse lernt den Namen dazu - der Rueckfall,
     * der ihn spaeter traegt, wenn sich die Adresse aendert.
     *
     * **Nur hier, nicht bei jedem Erreichbarkeitstest.** Der Name kommt aus
     * einer PTR-Abfrage; beim Suchlauf laeuft sie ohnehin, im Prueftakt waere
     * sie ein zusaetzlicher Netzumlauf je Favorit.
     */
    private fun adoptHostname(address: String, fqdn: String) {
        val favorite = favorites.list()
            .firstOrNull { it.address.equals(address, ignoreCase = true) } ?: return
        if (favorite.hostname.equals(fqdn, ignoreCase = true)) return
        if (favorites.isTaken(fqdn, favorite)) return
        favorites.replace(favorite, favorite.copy(hostname = fqdn))
    }

    /**
     * Grobe Formpruefung nach RFC 1123: Glieder aus Buchstaben, Ziffern und
     * Bindestrichen, durch Punkte getrennt, kein Bindestrich am Rand.
     *
     * Sie faengt Tippfehler ab - ein eingeschlichenes Leerzeichen, ein
     * vorangestelltes "http://". Ob der Name wirklich aufloest, sagt ohnehin
     * erst die Gegenprobe.
     */
    private fun isHostname(value: String): Boolean {
        if (value.isEmpty() || value.length > MAX_HOSTNAME) return false
        return value.split('.').all { label ->
            label.isNotEmpty() && label.length <= MAX_LABEL &&
                    label.all { (it.isLetterOrDigit() && it.code < 128) || it == '-' } &&
                    !label.startsWith('-') && !label.endsWith('-')
        }
    }

    /**
     * Ein Teil der Adresse: 0 bis 255, hoechstens drei Ziffern.
     *
     * **Fuehrende Nullen werden abgelehnt.** "010" ist zweideutig - manche
     * Aufloeser lesen es als Oktalzahl und landen bei 8 statt 10. Lieber eine
     * klare Meldung als eine Adresse, die woanders hinzeigt.
     *
     * Nur IPv4, weil auch die Suche und der Erreichbarkeitstest nichts anderes
     * kennen: Ein IPv6-Favorit waere ein Eintrag, der nie gruen wird.
     */
    private fun isAddressPart(part: String): Boolean =
        part.isNotEmpty() &&
                part.length <= 3 &&
                part.all { it.isDigit() } &&
                (part.length == 1 || part[0] != '0') &&
                part.toInt() <= 255

    /**
     * Verdrahtet die vier Felder zu einer Eingabemaske:
     *
     * - Nach der dritten Ziffer springt der Fokus von selbst weiter.
     * - Ein Punkt springt ebenfalls weiter, **ohne das naechste Feld zu leeren**.
     * - Rueckschritt in einem leeren Feld springt zurueck.
     * - Wer eine ganze Adresse in ein Feld tippt, bekommt sie verteilt.
     */
    private fun wireAddressMask(parts: List<EditText>, onEdit: () -> Unit) {
        parts.forEachIndexed { index, field ->
            field.doOnTextChanged { text, _, _, _ ->
                if (maskBusy) return@doOnTextChanged
                onEdit()

                val raw = text?.toString().orEmpty()
                when {
                    raw.contains('.') || raw.length > 3 -> distributeAddress(parts, index, raw)
                    raw.length == 3 && index < parts.lastIndex -> focusPart(parts[index + 1])
                }
            }

            field.setOnKeyListener { _, keyCode, event ->
                val goBack = keyCode == KeyEvent.KEYCODE_DEL &&
                        event.action == KeyEvent.ACTION_DOWN &&
                        field.text.isEmpty() &&
                        index > 0
                if (goBack) {
                    val previous = parts[index - 1]
                    previous.requestFocus()
                    previous.setSelection(previous.text.length)
                }
                goBack
            }
        }
    }

    /** Das Verteilen setzt selbst Text - ohne die Sperre riefe es sich erneut auf. */
    private var maskBusy = false

    private fun focusPart(field: EditText) {
        field.requestFocus()
        field.selectAll()
    }

    private fun distributeAddress(parts: List<EditText>, from: Int, raw: String) {
        val advanceAfter = raw.endsWith('.')
        val groups = raw.split('.')
            .flatMap { chunk -> chunk.filter { it.isDigit() }.chunked(3) }
            .filter { it.isNotEmpty() }

        maskBusy = true
        if (groups.isEmpty()) {
            // Nur ein Punkt: Das Zeichen gehoert nicht ins Feld.
            parts[from].setText("")
        } else {
            groups.forEachIndexed { offset, group ->
                val target = from + offset
                if (target <= parts.lastIndex) parts[target].setText(group)
            }
        }
        maskBusy = false

        // **Ein Punkt in einem leeren Feld springt nicht weiter.** Nach der
        // dritten Ziffer ist der Fokus schon von selbst gewandert; der danach
        // getippte Punkt landet dann im naechsten, noch leeren Feld. Wuerde er
        // dort erneut weiterspringen, bliebe ein Feld leer und die restlichen
        // Ziffern rutschten zusammen - aus 10.130.1.99 wurde so 10|130|_|199.
        if (groups.isEmpty()) {
            parts[from].setSelection(0)
            return
        }

        val lastFilled = minOf(from + groups.size - 1, parts.lastIndex)
        if ((advanceAfter || groups.last().length == 3) && lastFilled < parts.lastIndex) {
            focusPart(parts[lastFilled + 1])
        } else {
            parts[lastFilled].requestFocus()
            parts[lastFilled].setSelection(parts[lastFilled].text.length)
        }
    }

    /** Sortierschluessel, damit 192.168.1.9 vor 192.168.1.10 steht. */
    private fun addressKey(address: String): Long {
        val parts = address.split('.')
        if (parts.size != 4) return Long.MAX_VALUE
        var key = 0L
        for (part in parts) {
            val value = part.toIntOrNull() ?: return Long.MAX_VALUE
            key = key * 256 + value
        }
        return key
    }

    private companion object {
        /**
         * Android 17. Erst ab hier gibt es ACCESS_LOCAL_NETWORK; davor ist der
         * Zugriff aufs lokale Netz nicht gesondert geschuetzt.
         */
        const val API_LOCAL_NETWORK = 37

        /**
         * Als Zeichenkette statt ueber Manifest.permission: So bleibt der Code
         * frei von einer Konstante, die es erst ab API 37 gibt.
         */
        const val PERMISSION_LOCAL_NETWORK = "android.permission.ACCESS_LOCAL_NETWORK"

        /** Nur jede so vielte Adresse wird als Text nachgefuehrt. */
        const val PROGRESS_STEP = 16

        /** Materials Deckkraft fuer Abgeschaltetes - fuer den grauen Weg im Dialog. */
        const val DIMMED = 0.38f

        /**
         * mDNS-Namen weist der Dialog ab: Android loest sie von sich aus nicht
         * auf. So ein Favorit koennte nie gruen werden.
         */
        const val MDNS_SUFFIX = ".local"

        /** Laengengrenzen nach RFC 1035: der ganze Name und ein einzelnes Glied. */
        const val MAX_HOSTNAME = 253
        const val MAX_LABEL = 63
    }
}
