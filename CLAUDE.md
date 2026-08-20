# CLAUDE.md

Diese Datei ist die Übergabe an die nächste Sitzung. Das Gedächtnis eines Chats
endet mit ihm — was hier nicht steht, ist weg.

Kommunikation mit dem Nutzer erfolgt auf **Deutsch**.

**Diese Datei liegt im öffentlichen Repository.** Sie enthält deshalb keine
Namen, keine Wohnorte, keine Benutzerpfade, keine WLAN-Namen und keine
Adressen aus fremden Netzen. Wer hier etwas nachträgt, hält das ein.

---

## Die Namensregel — zuerst lesen

**Der Produktname des Pedals darf aus rechtlichen Gründen nirgends im Projekt
auftauchen.** Er ist eine eingetragene Marke. Betroffen sind Zeichenketten,
Kommentare, Dateinamen, Klassennamen und Vorschauwerte (`tools:text`). In der
Oberfläche heißt es durchgängig **„Gerät"**.

**Es gibt keine Ausnahme mehr.** Bis dahin stand der Projektname der
fremden Firmware als Protokollwert in `Model.kt` — die App verglich ihn Zeichen
für Zeichen, um ein Gerät zu erkennen. Mit der neuen Erkennung ist er
verschwunden: Die beiden Merkmale, an denen ein Gerät jetzt erkannt wird, sind
**bewusst so gewählt, dass sie markenfrei sind** (siehe „Wie die Geräte erkannt
werden"). Der Seitentitel hätte es getan und schied genau deshalb aus.

**Auch im Info-Bildschirm wird keine Marke namentlich genannt.** Der
Markenabschnitt bleibt allgemein gehalten und verweist auf die Namen, die in den
angezeigten Weboberflächen der Geräte vorkommen — die stammen vom Gerät, nicht
von dieser App.

### Wie das Firmware-Projekt genannt wird

**Es heißt „Builty's Controller"** — nach dem Namen, unter dem sein Urheber
auftritt, nicht nach dem Gerät. So entschieden am 20.08.2026. Diese Schreibweise
gilt überall: README, Info-Bildschirm, diese Datei. **Der Repository-Name wird
nirgends ausgeschrieben**, denn er trägt die Marke.

**Verlinkt wird auf das Benutzerprofil `https://github.com/Builty/`**, nicht auf
das Repository — im Info-Bildschirm wie in der README. Damit kommt die Marke im
ganzen Projekt **an keiner einzigen Stelle** mehr vor, auch nicht in einem
Linkziel. Das Firmware-Projekt steht auf jenem Profil obenan und ist von dort in
einem Klick erreichbar.

> **In der README wäre ein Repository-Link technisch unsichtbar gewesen** —
> Markdown zeigt nur den Linktext. Der Nutzer hat sich am 20.08.2026 trotzdem
> für das Profil entschieden, und das ist die strengere und einfachere Regel:
> eine Adresse, überall dieselbe, kein Sonderfall zu merken.
>
> **Im Info-Bildschirm ginge es ohnehin nicht anders.** Dort steht die Adresse
> als Klartext in einem `TextView` mit `autoLink` — `autoLink` färbt nur ein,
> es maskiert nichts. Ein Repository-Link stünde dort lesbar in der App.

Beide Texte sagen im Markenabschnitt, dass die dort und in den Weboberflächen
vorkommenden Namen ihren Inhabern gehören und aus ihrer Nennung keine Verbindung
folgt. **Dazu kommt ein Dank an Builty** — in der README wie im
Info-Bildschirm.

Aus demselben Grund ist in dieser Datei der **Ordnername des Firmware-Projekts
nicht ausgeschrieben**; wo sein Quelltext gebraucht wird, steht der Pfad als
Platzhalter.

---

## Was dieses Projekt ist

Eine Android-App, die die **Weboberflächen der ESP32-S3-Geräte im WLAN**
anzeigt. Die App findet Geräte, merkt sie sich und öffnet ihre Seite in einer
WebView. Kein eigenes Protokoll, keine Firmware-Verwaltung, **keine
Gerätesteuerung** — sie spielt nichts auf, lädt nichts hoch und setzt nichts.

> **In der Vorgeschichte war das anders.** Die App konnte ein Gerät in eine
> zweite Firmware starten lassen — die gehört zu einer abgewandelten Fassung
> des Firmware-Projekts, **der Original-Controller hat sie nicht**. Mit der
> Ausrichtung auf ihn ist die Funktion entfallen, und mit ihr die Begründung,
> die Grenze je überschritten zu haben. Der Haftungstext im Info-Bildschirm
> sagt deshalb wieder, was er ursprünglich sagte: Die App zeigt nur an.

**Für die Zahl der Favoriten gibt es bewusst keine Obergrenze.** Ausgelegt ist
die Oberfläche auf rund zwanzig; mehr gehen auch, dann laufen die
Erreichbarkeitsprüfungen nur in mehreren Wellen.

| | |
| --- | --- |
| Paket | `io.github.ft972.patchmanul` |
| Sprache | Kotlin, klassische Views (**kein** Compose) |
| minSdk / targetSdk / compileSdk | 26 / 37 / 37 |
| Veröffentlichung | `https://github.com/ft972/PatchManul` |

> **Die Paketkennung wurde am 20.08.2026 umgestellt.** Die alte trug
> eine private Domäne. `io.github.<benutzername>.<app>` ist die übliche Wahl
> für ein Projekt ohne eigene Domäne, und sie passt zu dem Namen, unter dem der
> Urheber ohnehin auftritt.
>
> **Für Android ist eine geänderte Kennung eine andere App**: kein
> Aktualisierungsweg, die alte Installation bleibt daneben stehen, ihre
> Favoriten sind verloren. Deshalb fand der Wechsel **vor** der ersten
> Veröffentlichung statt — danach hätte er jeden Nutzer getroffen. Wer sie je
> wieder anfasst, weiß damit, was es kostet.
>
> Zu ändern sind zwei Angaben im Buildscript (`namespace` und `applicationId`),
> der Ordnerpfad unter `java/` und die `package`-Zeile jeder Quelldatei. **Das
> Manifest bleibt unberührt** — es benennt seine Klassen relativ
> (`.MainActivity`) und zieht von selbst mit.

---

## Stand am 20.08.2026 — hier weitermachen

**Fassung 1.0.0**, gebaut, signiert und lintsauber (**0 Fehler, 2 Warnungen**).
Die App ist auf den **Original-Controller** ausgerichtet.

> **Die Zählung fängt hier an.** Vor der Veröffentlichung gab es eine längere
> Vorgeschichte unter anderer Paketkennung und mit Fassungsnummern bis 2.0.1;
> sie ist mit der Ausrichtung auf den Original-Controller hinfällig geworden.
> `CHANGELOG.md` beginnt deshalb bei **1.0.0 / versionCode 1**, und keine
> dieser alten Nummern ist je veröffentlicht worden. **Ab hier steigt
> versionCode wieder bei jeder ausgelieferten Fassung.**

**Am Gerät belegt, am selben Tag.** Der Suchlauf hat den Controller im
eigenen /24 gefunden, die Favoritenzeile stand auf „erreichbar", die
Weboberfläche lud, und die Zeilenmenüs zeigten genau das, was übrig bleiben
sollte. **Und es war wirklich der Original-Controller**: Der alte Endpunkt
`/api/ota/info` antwortete auf demselben Gerät mit **404** — die
Vorgängerfassung hätte es also gar nicht gefunden. Die Einzelheiten stehen unter
„Was am Gerät belegt ist".

Was in dieser Sitzung geschah:

- Die Erkennung läuft nicht mehr über einen Status-Endpunkt, sondern über die
  **Startseite** des Geräts.
- **Alles zur zweiten Firmware ist entfernt**: Umschalten, Wartungsseite,
  Umschaltleiste, gelber Zeilenzustand, der eigene Prüftakt der Webansicht.
- Die **Merkhilfe zu den Globals** ist entfernt.
- Eine **README** ist entstanden (englisch).
- Klarnamen und Rechnerpfade sind aus der Dokumentation verschwunden.
- Die **Paketkennung** heißt `io.github.ft972.patchmanul`; die alte trug eine
  private Domäne.
- Der **Info-Bildschirm** nennt jetzt, wozu die App gehört, und **dankt Builty**
  für die Firmware. Die **README** ist zweisprachig (Englisch oben, Deutsch
  darunter).
- **Changelog und Versionszählung fangen bei 1.0.0 an.**

### Was als Nächstes lohnt

1. Vor der Veröffentlichung: die **Rechtstexte** von jemandem mit
   Sachkenntnis ansehen lassen (Punkt 2) — der einzige Punkt, der wirklich
   zählt.
2. Der **Namensweg in einem Netz mit Rückwärtszone** (Punkt 4) und die
   **Wirkung der Zeitgrenze** (Punkt 5). Beides sind alte Lücken, keine neuen.
3. Den **Release-Build auf das Testgerät bringen**, wenn es sich lohnt: Dort
   liegt eine Debug-Fassung, und ein Wechsel verlangt Deinstallation samt
   Verlust der Favoriten. Für die Prüfung war das nicht nötig — der Code ist
   derselbe.

> **Der Ordner `.patchmanul` im Benutzerverzeichnis gehört gesichert**, und zwar
> woanders hin als auf dieselbe Platte. Ohne den Release-Schlüssel lässt sich
> die App nie wieder aktualisieren — siehe Punkt 3.

---

## Die Vorgeschichte

**`CHANGELOG.md` beginnt bei 1.0.0, und das ist der Stand, der zählt.** Wer
wissen will, warum etwas so ist, wie es ist, findet die Begründungen unter
„Entscheidungen und ihre Gründe" — die stehen dort ohne Fassungsnummern, weil
sie an der Sache hängen und nicht an einer Zahl.

Ein paar Dinge aus der Zeit davor gehören trotzdem hierher, weil sie sonst
niemand mehr wüsste:

- **Die App war ursprünglich auf eine abgewandelte Firmware zugeschnitten**, die
  zwei Betriebsarten auf einem Gerät hielt und dafür einen Status-Endpunkt
  mitbrachte. Daran hingen das Umschalten zwischen beiden, eine Wartungsseite,
  eine Umschaltleiste in der Webansicht, ein eigener Prüftakt dort und eine
  Merkhilfe zu Geräteparametern. **Alles davon ist entfallen** — der
  Original-Controller kennt nichts davon. Wer im Quelltext auf Reste stößt, hat
  eine alte Kopie vor sich.
- **Die Erkennung lief über `/api/ota/info`.** Diesen Pfad gibt es beim
  Original-Controller nicht; er antwortet dort mit 404.
- **Die Paketkennung trug eine private Domäne** und heißt jetzt
  `io.github.ft972.patchmanul`.
- **Der Release-Schlüssel wurde einmal gewechselt**, weil der erste den
  bürgerlichen Namen im Zertifikat trug — auslesbar aus jedem APK. Siehe „Die
  Release-Signatur".

**Lint stand über die ganze Zeit auf 0 Fehlern.** Aktuell sind es **2
Warnungen**: `GradleDependency` (Lints Online-Abgleich mit Maven) und
`Overdraw`. **Keine Fassung hat je eine neue hinzugefügt**; wer eine sieht, hat
sie sich gerade eingehandelt.

---

## Wie die Geräte erkannt werden

**Das ist der Kern der App.**

Ein Gerät gilt als solches, wenn `GET http://<ip>/` mit **HTTP 200**, dem
Inhaltstyp **text/html** und einer Seite antwortet, in der **beide** dieser
Zeichenketten vorkommen:

```
id="set_preset"
onPresetChange(
```

Sie stehen in den ersten rund anderthalb Kilobyte der Seite — gelesen werden
höchstens 8 KB, die Prüfung greift also weit innerhalb.

### Warum die Startseite und kein Endpunkt

Am 20.08.2026 im Quelltext der Original-Firmware nachgelesen. Ihr Webserver
registriert **genau zwei** Dinge:

| Pfad | Was dort liegt |
| --- | --- |
| `/*` | die eingebetteten Dateien: `index.html`, CSS, JavaScript, die PNGs der Oberfläche |
| `/ws` | ein WebSocket — darüber läuft **alles**, was die Seite an Parametern liest und setzt |

Jeder andere Pfad wird mit **404** beantwortet. Es gibt **keinen Pfad, der etwas
über das Gerät aussagt**: keine Kennung, keine MAC, keinen Namen, keine Version.

> **Die Vorgängerfassung fragte `/api/ota/info` ab.** Diesen Endpunkt bringt
> eine abgewandelte Firmware mit, der Original-Controller nicht. Wer ihn hier
> wiederfindet, hat eine alte Fassung vor sich.

Damit bleibt die Seite selbst. Die beiden Merkmale gehören zur Preset-Auswahl in
ihrem Kopfbereich; alles im Netz, was keine solche Seite ausliefert — Router,
Drucker, NAS, Kameras —, fällt durch.

> **Die beiden Zeichenketten sind Protokollwerte, keine Bezeichnungen.** Ändert
> die Firmware ihre Seite an dieser Stelle, findet die App nichts mehr. Das ist
> der Preis dafür, dass es keinen dafür gedachten Endpunkt gibt, und er ist
> bekannt. Sie stehen in `BoardProbe.MARKERS`, an einer Stelle, mit Kommentar.

### Was sonst noch ginge — und warum es nicht genommen wurde

| Weg | Warum nicht |
| --- | --- |
| **mDNS** | Die Firmware betreibt einen Responder, und sein Name ist einstellbar — aber **Android löst `.local` von sich aus nicht auf**, und der Vorgabename trägt die Marke. Ein Suchlauf über `NsdManager` wäre ein eigener Weg neben dem Sweep, kein Ersatz |
| **UDP-Locater** | Die Firmware sendet alle drei Sekunden ihre IP als XML per Broadcast auf **Port 12106**. Das ist der für die Suche *gedachte* Weg und wäre schneller als 253 Anfragen — aber er ist passiv (man muss warten), Broadcasts werden in manchen Netzen gefiltert, und im Paket steht nur die Adresse. **Als Ergänzung lohnt er, als Ersatz nicht** (offener Punkt 6) |
| **WebSocket** | Er sagt am meisten über das Gerät — verlangt aber einen Client, den die App nicht hat und der eine Fremdbibliothek oder ein selbstgeschriebenes RFC 6455 kostete. Für die Frage „Gerät oder nicht" ist das weit überzogen |

### Was die Firmware über sich preisgibt — und was nicht

- **Keinen DHCP-Hostnamen.** `esp_netif_set_hostname()` kommt in ihrem ganzen
  `main/` nicht vor; sie meldet dem Router also den Vorgabewert der ESP-IDF, und
  der ist auf **jedem** Board derselbe. Das ist der Grund für
  `dropAmbiguousNames()` (siehe unten).
- **Keine Version über HTTP.** Was die Weboberfläche anzeigt, holt sie sich über
  den WebSocket.

---

## Aufbau

Elf Kotlin-Dateien, sieben Layouts. Keine Fremdbibliothek über die
AndroidX-Grundausstattung hinaus.

| Datei | Inhalt |
| --- | --- |
| `Model.kt` | `BoardInfo` (nur die Adresse), `Favorite`, `Route` |
| `BoardProbe.kt` | **Die Erkennung**: eine HTTP-Anfrage auf `/`, Inhaltstyp prüfen, zwei Merkmale im Text suchen |
| `NetworkScanner.kt` | Subnetz vermessen und durchsuchen, 40 Anfragen gleichzeitig — **die Zeitgrenze holt er aus den Einstellungen** |
| `ReachabilityCheck.kt` | Der Erreichbarkeitstest für beide Listen (rot/grün), **mit Auflösung und Rückfall** |
| `HostnameLookup.kt` | Rückwärtsauflösung, **Gegenprobe** und die einmalige Probe aus dem Dialog |
| `FavoritesStore.kt` | Favoriten als JSON in den SharedPreferences, **ohne festen Schlüssel** |
| `SettingsStore.kt` | Prüftakt, **Zeitgrenze der Suche**, Farbmodus, Sprache; dazu die Wertelisten für alle vier Auswahlfelder |
| `PatchManulApplication.kt` | Wendet Farbmodus und Material-You-Akzentfarbe vor der ersten Activity an |
| `MainActivity.kt` | Beide Listen, Suchknopf, Dialoge, Adressmaske, Merkmalswahl, beide Zeilenmenüs |
| `SettingsActivity.kt` | **Die Einstellungen als eigene Seite** — vier Auswahlfelder, jede Wahl gilt sofort |
| `InfoActivity.kt` | Der Info-Bildschirm mit den rechtlichen Angaben |
| `LicenseActivity.kt` | Zeigt den Apache-2.0-Lizenztext aus `res/raw` |
| `WebActivity.kt` | Die WebView, Home-Knopf, Neu laden |
| `res/layout/activity_main.xml` | Kopfzeile, Suchliste, Favoritenliste in einem `NestedScrollView` |
| `res/layout/item_device.xml` | Eine Listenzeile, für beide Listen dieselbe |
| `res/layout/dialog_favorite.xml` | Name, **Merkmalsschalter**, die vier Adressfelder, das **Namensfeld** und zwei Fehlerzeilen |
| `res/layout/activity_settings.xml` | Kopfzeile mit Zurück-Knopf, darunter die vier Einstellungen in einem `NestedScrollView` |
| `res/layout/activity_web.xml` | Kopfzeile mit **Home-Knopf**, Fortschritt, WebView |
| `res/layout/activity_info.xml` | Der Info-Bildschirm, mit dem Knopf zum Lizenztext |
| `res/layout/activity_license.xml` | Der Lizenztext — **in beide Richtungen scrollbar**, sonst zerreißt der Umbruch die Vorlage |
| `res/raw/apache_2_0.txt` | Der Apache-2.0-Lizenztext, **aus den Bibliotheken selbst entnommen** |
| `res/menu/favorite_actions.xml` | Bearbeiten / Entfernen |
| `res/menu/scan_actions.xml` | Merken — der einzige Punkt |
| `res/values/colors.xml` | Die drei Statusfarben und der Grundton des Startsymbols |
| `res/xml/network_security_config.xml` | Klartext-HTTP freigegeben |
| `res/xml/locales_config.xml` | Die vier mitgelieferten Sprachen |
| `res/xml/backup_rules.xml`, `data_extraction_rules.xml` | **Vollständiger Ausschluss von der Datensicherung** — bis Android 11 die eine, ab 12 die andere |
| `res/values` | **Die Grundfassung — Englisch**, dazu alle nicht übersetzbaren Einträge |
| `res/values-de`, `-fr`, `-es` | Übersetzungen |
| `res/values*/strings_info.xml` | **Die Texte des Info-Bildschirms — nur Englisch und Deutsch**, samt Begründung im Kopf der Datei |
| `tools/make_icons.ps1` | Baut alle Symbolebenen aus `icon.png` |
| `CHANGELOG.md` | Die Fassungen, neueste zuerst — **beginnt bei 1.0.0** |
| `README.md` | **zweisprachig, Englisch oben, Deutsch darunter**; die Linkziele stehen als Referenzen am Dateiende |
| `LICENSE` | MIT-0 im Original-Wortlaut, dazu die Abgrenzung zu den Bibliotheken |

### Die Oberfläche

Von oben nach unten: eine Kopfzeile mit den Symbolen für **Einstellungen** und
**Info**, darunter die **Suchliste**, darunter die **Favoritenliste**.

> **Diese Reihenfolge ist so gewollt** (getauscht am 08.08.2026). Wer sie wieder
> umdreht, kippt eine ausdrückliche Entscheidung.

Der Ablauf in Worten:

1. **Beim Start und bei jeder Rückkehr** prüft `ReachabilityCheck` alles, was
   angezeigt wird — Favoriten **und** Fundstellen. Grün heißt „erreichbar", rot
   „nicht erreichbar", grau „Prüfung läuft noch". Bei einem Favoriten wird
   zuerst sein bevorzugtes Merkmal probiert und danach das andere; in der
   zweiten Zeile steht, was geantwortet hat.
2. **Auf „Suchen"** ermittelt `NetworkScanner` das eigene Subnetz und fragt jede
   Adresse einmal. Treffer erscheinen sofort, nicht erst am Ende.
3. **Danach** fragt `HostnameLookup` für die gefundenen Adressen den Namen beim
   DNS-Server nach und probt ihn gleich gegen — zeigt er vorwärts auf dieselbe
   Adresse zurück?
4. **Auf „Merken"** öffnet der Dialog mit vorbelegter Adresse, Namensvorschlag
   und dem Merkmal, das die Gegenprobe bestanden hat.
5. **Auf das Drei-Punkte-Symbol** einer Favoritenzeile öffnet ein Menü mit
   **Bearbeiten** und **Entfernen**. Die Suchzeile hat dasselbe Symbol; dort
   steht nur **Merken**, und ist das Gerät schon gemerkt, **erscheint das Symbol
   gar nicht erst** — das Menü wäre leer.
6. **Auf eine Zeile** öffnet `WebActivity` die Seite unter `http://<ip>/`.

Das Zahnrad öffnet die **Einstellungen** (Prüftakt, Zeitgrenze der Suche, Design
und Sprache), das Info-Symbol den **Info-Bildschirm**. **Beide sind eine eigene
Activity**, und aus demselben Grund: Der Inhalt ist für einen Dialog zu lang und
muss gescrollt werden.

Während eines Suchlaufs zeigt ein Fortschrittsbalken den Stand, darunter „x von
y Adressen". Der Suchknopf ist so lange gesperrt. Der Balken läuft bei jeder
Adresse mit, **die Zahlen darunter nur bei jeder sechzehnten**
(`PROGRESS_STEP`) — vierzig Threads melden sonst schneller, als sich ein Text
lesen lässt.

Die Suchliste ist **nach Adresse sortiert**, nicht nach Fundzeitpunkt
(`addressKey()`). Der Schlüssel rechnet die vier Glieder zu einer Zahl zusammen,
damit `192.168.1.9` vor `192.168.1.10` steht — bei einem Textvergleich wäre es
umgekehrt. Die Favoritenliste behält die Reihenfolge, in der die Einträge
angelegt wurden.

> **Ein Favorit wird über Adresse *oder* Name geführt.** Beim Bearbeiten wird
> der alte Eintrag deshalb grundsätzlich entfernt, bevor der neue kommt — jedes
> der beiden Merkmale kann sich geändert haben, und sonst stünden anschließend
> zwei da. Das ist in `showFavoriteDialog()` abgefangen; beim Ändern dieser
> Stelle daran denken.

---

## Entscheidungen und ihre Gründe

Alles hier wurde bewusst so gewählt. Wer es ändern will, sollte den Grund kennen.

### Views statt Compose

Entschieden, als der Entwicklungsrechner noch 8 GB RAM hatte. Der Speicher ist
inzwischen auf 14 GB gewachsen, die **CPU ist aber weiterhin eine von 2013**,
und der Compose-Compiler verlängert jeden Build spürbar — diese Zeit zahlt der
Nutzer bei jedem Durchlauf. Der Mehraufwand an Boilerplate fällt dagegen bei der
KI an.

**Ein Wechsel lohnt jetzt nicht:** Die App ist fertig. Ein Umbau wäre Arbeit
ohne Gegenwert und würde jede Prüfung entwerten.

### Kein RecyclerView

Bei rund zwanzig Favoriten und einer Handvoll Fundstellen werden beide Listen
bei jeder Änderung komplett neu aufgebaut — `removeAllViews()` plus `inflate()`
in einer Schleife. Das ist billiger als ein Adapter und deutlich weniger Code.
**Bei deutlich längeren Listen wäre das die erste Stelle zum Nachziehen.**

### `ExecutorService` statt Coroutines

Spart eine Abhängigkeit. `kotlinx-coroutines` ist nicht eingebunden, und AGP 9
bringt Kotlin ohne separates Plugin mit. Die drei Hintergrundklassen folgen alle
demselben Muster: fester Pool, Ergebnisse über einen `Handler` auf den
Hauptthread, `pool !== executor` als Abbruchprüfung.

### Klartext-HTTP pauschal freigegeben

Seit Android 9 ist HTTP ohne TLS gesperrt. Die Freigabe steht als `base-config`
für die **ganze App**, nicht auf eine Adresse begrenzt.

**Warum nicht enger:** Eine `domain-config` nimmt nur feste Hostnamen oder
einzelne IP-Adressen an, **keine Adressbereiche**. Die Adresse eines Geräts
wechselt mit dem DHCP-Lease, und der Suchlauf probiert ohnehin ein ganzes
Subnetz durch. Eine Liste fester Einträge wäre hier nicht eng, sondern kaputt.

Die Lint-Warnung `InsecureBaseConfiguration` ist deshalb mit `tools:ignore`
stummgeschaltet — sonst verdeckt sie bei jedem Lauf die echten Befunde.

### Gesucht wird im eigenen /24 — größer nur auf Nachfrage

Ein /16 wären 65 534 Adressen und damit Minuten statt Sekunden; das Gerät hängt
am selben Access Point wie das Handy.

Ist das Netz größer als /24, **fragt die App vor dem Suchlauf nach**
(`NetworkScanner.survey()` liefert die Zahlen, `MainActivity.startScan()` stellt
den Dialog). Angeboten werden „Nur /24 durchsuchen" und „Alles durchsuchen", mit
der geschätzten Dauer im Text. Ohne diese Rückfrage sähe ein langer Lauf aus,
als hinge die App.

**Unter /16 wird auch auf Wunsch nicht gesucht** (`MIN_FULL_PREFIX`). Ein /8
wären über sechzehn Millionen Adressen — das liefe tagelang. Dort erscheint nur
der Hinweis, dass die Suche nicht alles abdeckt.

Die Schätzung rechnet schlicht `Adressen ÷ 40 Threads × Zeitgrenze` — sie zählt
die Zeitgrenze, die für jede tote Adresse voll anfällt, und nimmt den
**eingestellten** Wert.

### Die Zeitgrenze der Suche ist einstellbar — 600 bis 900 ms

Eingebaut am 19.08.2026 auf ausdrücklichen Wunsch: **In einem trägen Netz werden
nicht alle Geräte gefunden.** Ein ESP32-S3 ist kein schneller Server, und ein
überlastetes oder weit gespanntes WLAN kostet zusätzlich Millisekunden — trifft
die Antwort nach der Zeitgrenze ein, ist die Adresse längst als tot abgehakt.

| | |
| --- | --- |
| **Minimum und Vorgabe** | 600 ms — wer nichts umstellt, sucht wie zuvor |
| **Maximum** | 900 ms, das **Anderthalbfache** des Minimums |
| **Stufen dazwischen** | 700 und 800 ms |
| **Gespeichert unter** | `scan_timeout_ms` in denselben SharedPreferences wie der Rest |

**Der Lesezeitraum wächst mit, im Verhältnis 2,5 zu 1** (`readTimeoutFor()`).
Wer die Verbindung länger warten lässt, will auch der Antwort mehr Zeit geben —
langsam ist selten nur der Verbindungsaufbau.

Drei Dinge, die daran hängen:

- **Der Wert wird einmal je Lauf gelesen**, nicht je Adresse. Alle Anfragen
  eines Laufs arbeiten mit derselben Zeitgrenze, auch wenn nebenher etwas
  umgestellt wird.
- **Die Dauerschätzung zieht mit.**
- **Der Erreichbarkeitstest bleibt unberührt.** Er hat eigene Zeitgrenzen
  (1,5 s / 2,5 s) und ein anderes Problem: Dort geht es um eine Handvoll
  bekannter Ziele, nicht um 253 Adressen, von denen fast alle tot sind.

> **Die Erkennung liest eine große Antwort an.** Die Startseite ist rund
> 138 KB groß; die App liest davon nur die ersten 8 KB und bricht dann ab. Ob
> das den Lesezeitraum spürbar belastet, ist **nicht gemessen** — belegt ist
> nur, dass ein Lauf über 253 Adressen in Sekunden durch ist.

### Der Erreichbarkeitstest umfasst beide Listen

`checkDevices()` prüft die Favoriten **und** die Fundstellen des letzten
Suchlaufs. Beide führen ihre Ziele im selben Statusspeicher `status`, ein Gerät
in beiden Listen wird also nur einmal gefragt und beide Zeilen zeigen denselben
Stand.

> Der Schlüssel ist der Zeilenschlüssel (`Favorite.key`), und das Ergebnis wird
> **zusätzlich unter der bestätigten IP** abgelegt. Nur so teilt ein über den
> Namen geführter Favorit seinen Stand mit der Fundstelle desselben Geräts.

Die Folge rechtfertigt den Aufwand: Eine Fundstelle kann **rot** werden. Vorher
blieb sie für immer grün, weil sie beim Suchlauf ja erreichbar war — auch wenn
das Gerät längst weg war.

Der Fund selbst zählt als Prüfung: `onBoardFound()` trägt sein Ergebnis gleich
in `status` ein. Ein Favorit unter derselben Adresse wird dadurch sofort grün.

### Ein nicht erreichbares Gerät lässt sich gar nicht öffnen

Wer eine Zeile antippt, deren Prüfung durch ist und die nichts geantwortet hat,
bekommt den Dialog „Nicht erreichbar" — mit **„Abbrechen"** und **„Erneut
prüfen"**, sonst nichts.

**Einen Knopf „Trotzdem öffnen" gab es bis zum 09.08.2026, er ist entfernt**
(ausdrücklich so entschieden). Wer nicht antwortet, hat auch keine Seite zu
zeigen: Der Knopf führte zuverlässig auf die Fehlermeldung der WebView. Übrig
bleibt der Weg, der etwas ändern kann — neu prüfen, und wenn das Gerät wirklich
weg ist, hilft nur ein Suchlauf.

Der zugehörige Text `offline_open_anyway` ist in allen vier Sprachen gelöscht;
sonst hätte Lint ihn als ungenutzt gemeldet.

### Der wiederkehrende Test läuft nur im Vordergrund

Der Takt ist in den Einstellungen wählbar (aus, 15 s, 30 s, 1 min, 5 min),
**Vorgabe ist aus**. Er hängt an einem `Handler` in `MainActivity` und wird in
`onPause()` abgeräumt.

**Kein Hintergrunddienst.** Für „ist das Gerät gerade da" wäre ein
Foreground-Service samt Dauerbenachrichtigung unverhältnismäßig, und ohne einen
solchen würde Android die Prüfung ohnehin bald einstellen.

Beim wiederkehrenden Lauf werden die Markierungen **nicht** zurückgesetzt
(`checkDevices(resetStatus = false)`) — sonst flackerte die Liste im Takt grau.

### Vier Sprachen, umgeschaltet über AppCompat

**Englisch ist die Grundfassung in `res/values`**, dazu Deutsch in `values-de`,
Französisch und Spanisch. Umgeschaltet wird mit
`AppCompatDelegate.setApplicationLocales()`; das speichert die Wahl selbst und
baut den Bildschirm neu auf.

> **Der Default ist das, was jede nicht mitgelieferte Sprache zu sehen bekommt**
> — dafür taugt Englisch besser als Deutsch. Beim Tauschen war die Tücke, dass
> `translatable="false"`-Einträge nur im Default stehen dürfen: `app_name`, die
> Sprachnamen, die Adressmaske. Ein Duplikat in `values-de` wäre ein
> Lint-Fehler.

#### Der Info-Bildschirm hat nur zwei Sprachen

Seine Texte stehen **nicht** in `strings.xml`, sondern in `strings_info.xml` —
und die gibt es nur in `values` (Englisch) und `values-de`. Wer die App auf
Französisch oder Spanisch betreibt, bekommt die Oberfläche übersetzt und **den
Info-Bildschirm auf Englisch**.

**Der Grund ist inhaltlich, nicht technisch:** Dort stehen Lizenz-, Datenschutz-
und Haftungsaussagen. Französisch und Spanisch hat niemand gegengelesen, und
eine ungeprüfte Übersetzung eines Haftungstextes ist schlechter als gar keine.

> **Am Wurzelelement der englischen Datei steht `tools:ignore="MissingTranslation"`.**
> Ohne das meldet Lint für jeden dieser Texte eine fehlende Übersetzung. **Die
> Ausnahme steht bewusst nur dort** — in `strings.xml` soll eine fehlende
> Übersetzung weiterhin auffallen. Genau deshalb sind es zwei Dateien.

Zwei Dinge gehören dazu und dürfen nicht verschwinden:

- `android:localeConfig="@xml/locales_config"` im Manifest — sonst zeigt Android
  ab Version 13 die App nicht in seiner eigenen Sprachauswahl.
- Der Dienst `AppLocalesMetadataHolderService` mit `autoStoreLocales`. **Ohne
  ihn vergisst die App die Sprache auf Geräten unter Android 13 bei jedem
  Start.**

### Die Einstellungen sind eine eigene Seite, kein Dialog

**Der Anlass war der gedrehte Bildschirm:** Im Querformat blieb vom Dialog nur
ein flacher Streifen, in dem die unteren Auswahlfelder abgeschnitten waren — und
ein `AlertDialog` scrollt seinen Inhalt nicht von selbst.

**Speichern und Abbrechen sind entfallen — jede Wahl gilt sofort.** Das ist hier
das einzig verlässliche Verhalten: Design und Sprache bauen den Bildschirm beim
Umschalten neu auf, und ein Abbrechen wäre eine Lüge, sobald das Design schon
umgeschaltet ist.

Daraus folgt eine Regel: **Jede Aktion eines Auswahlfelds muss folgenlos
bleiben, solange sich nichts ändert.** Ein `Spinner` meldet seine
Anfangsstellung beim Aufbau noch einmal, und die Seite entsteht nach jedem
Design- oder Sprachwechsel neu. `applyThemeMode()` und `applyLanguage()` prüfen
das ausdrücklich.

**Der Prüftakt wird hier nur gespeichert, nicht neu gestellt.** `MainActivity`
plant ihn in `onResume()` ohnehin, und dorthin führt der einzige Weg zurück.

### Design und Material You

`AppCompatDelegate.setDefaultNightMode()` für Hell/Dunkel/System — **anders als
bei der Sprache speichert AppCompat diese Wahl nicht von selbst.** Deshalb gibt
es `PatchManulApplication`: Beim Start des Prozesses liest sie
`SettingsStore.themeMode` und wendet ihn an, bevor die erste Activity entsteht.
Ohne diese Stelle fiele die Wahl bei jedem Neustart auf den Systemmodus zurück.

Dieselbe Klasse ruft `DynamicColors.applyToActivitiesIfAvailable(this)` — ein
Aufruf, sonst nichts. Wo Android eine Farbe aus dem Hintergrundbild anbietet
(12+), ersetzt sie die feste Akzentfarbe; sonst bleibt es folgenlos. **Keine
Einstellung dafür**, passend zur Zurückhaltung der Bibliothek selbst. **Keine
Abhängigkeit dazugekommen** — `DynamicColors` gehört zu Material Components, das
ohnehin eingebunden ist.

### Der Home-Knopf in der Webansicht

Zwei unterschiedliche Wege zurück, mit Absicht:

| Weg | Verhalten |
| --- | --- |
| Home-Knopf oben links (`goHome()`) | **immer** direkt zur Geräteliste, unabhängig vom Seitenverlauf |
| Zurück-Taste des Systems (`goBack()`) | blättert **erst durch den Seitenverlauf**, verlässt den Bildschirm erst danach |

`goHome()` ruft schlicht `finish()` — `MainActivity` steht bereits als vorige
Activity im Stapel.

### Die Webansicht zeigt nur an

Sie hat **keine Umschaltleiste und keinen eigenen Prüftakt**. Beides gab es in
der Vorgeschichte und diente einer Frage, die sich nicht mehr stellt: welche von
zwei Firmwares gerade läuft.

Geblieben ist das Nötige: Home, Neu laden, Fortschritt, Fehlerzeile und eine
Adresszeile, die dem folgt, was tatsächlich zu sehen ist. `showPage()` wird aus
`onPageStarted`, `onPageFinished` **und `doUpdateVisitedHistory`** aufgerufen —
ohne den dritten stünde nach dem Zurückblättern die vorige Adresse dort, denn
die Geräteseite arbeitet mit der History-API, bei der `onPageStarted` nicht
feuert.

`javaScriptEnabled` und `domStorageEnabled` sind Pflicht: Die Oberfläche baut
ihren Inhalt per JavaScript auf und bliebe sonst leer. `LOAD_NO_CACHE`, weil ein
Geräte-Dashboard den Ist-Zustand zeigen soll.

> **Die Geräte-Weboberfläche ist breiter als der Bildschirm.** Sie ist für
> Desktop-Browser gebaut; Überschrift und der Menüpunkt ganz rechts laufen
> hinaus. Herauszoomen geht per Zwei-Finger-Geste (`builtInZoomControls`).

### Kein mDNS

Läge nahe, taugt aber nicht: **Android löst `.local` von sich aus nicht auf.**
Der Sweep ist der Weg, der ohne Zusatzarbeit trägt.

Aus demselben Grund nimmt der Favoriten-Dialog keine `.local`-Namen entgegen
(`error_hostname_mdns`) — ein solcher Favorit könnte nie grün werden.

### Ein Favorit wird über IP-Adresse *oder* DNS-Name geführt

Der ausführlichste Abschnitt, und das mit Absicht: Hier steckt die meiste
Überlegung.

**Der Ausgangspunkt:** Favoriten hingen an der IP-Adresse, und die wechselt mit
dem DHCP-Lease. Ein Name wäre stabiler — aber nicht überall und nicht immer.

| | überlebt neuen Lease | eindeutig |
| --- | --- | --- |
| IP-Adresse | nein | ja |
| DNS-Name | **ja** | nicht zwangsläufig |

**Immer beides speichern.** Ein `Favorite` führt vier Angaben: Anzeigename,
`route` (das bevorzugte Merkmal), `address` und `hostname`. Der Ablauf:

1. Bevorzugtes Ziel auflösen und abfragen.
2. Nichts? Dann das andere.
3. Was geantwortet hat, wird zum Stand — die bestätigte IP wird zurückgeschrieben.

Damit heilt der Eintrag den Ausfall selbst: neuer Lease → der Name trägt, die IP
wird nachgezogen.

> **Der nicht gewählte Weg wird ausgegraut, nicht entfernt.** Ihn wegzunehmen
> hieße, das Sicherheitsnetz einzurollen.

**Aufgelöst wird in `ReachabilityCheck`, nicht erst in der HTTP-Verbindung.** So
steht die IP hinterher fest und lässt sich nachtragen — und der Statusspeicher
bleibt nach IP verschlüsselt, sodass ein namensgeführter Favorit und die
Fundstelle desselben Geräts denselben Stand zeigen.

#### Die Gegenprobe — warum rückwärts nicht genügt

Der Namensvorschlag braucht nur IP → Name (PTR). Wer sich über den Namen
verbinden will, braucht **Name → IP (A)**, und das ist eine andere DNS-Funktion:
Ein Router kann die eine beherrschen und die andere nicht. `HostnameLookup` probt
deshalb jeden gefundenen Namen gleich gegen — nur wenn er vorwärts auf
**dieselbe** Adresse zeigt, gilt er als Merkmal und wird im Dialog vorbelegt.

Das erschlägt zwei Fälle: den Router ohne A-Records und den Fall, dass sich ein
**anderes** Gerät den Namen geschnappt hat.

Dazu eine zweite Regel: **Zeigen zwei Fundstellen desselben Laufs auf denselben
Namen, taugt er für keine von beiden** (`dropAmbiguousNames()`). Das ist kein
theoretischer Fall: Die Firmware meldet dem Router **keinen eigenen
DHCP-Hostnamen**, bei mehreren Geräten heißen also alle gleich. Bewusst ohne
fest eingetragene Zeichenkette — die Regel greift für jeden doppelten Namen.

#### Automatik mit unsichtbarer Feststellung

**Zwei sichtbare Stellungen plus ein gespeichertes `routeLocked`.** Solange
niemand den Schalter anfasst, zieht der Weg dem nach, was tatsächlich geantwortet
hat (`learn()`). Beim ersten Antippen wird die Stellung festgenagelt. Unter dem
Schalter steht, woran man ist.

Die Automatik kostet **keine einzige zusätzliche Abfrage** — sie wertet nur aus,
was die Erreichbarkeitsprüfung ohnehin ergibt.

#### Zwei Sparsamkeiten, die Absicht sind

- **Der Name wird nur beim Suchlauf nachgelernt** (`adoptHostname()`), nicht bei
  jeder Prüfung. Er käme aus einer PTR-Abfrage, und die wäre im Prüftakt ein
  eigener Netzumlauf **je Favorit**.
- **Geschrieben wird nur bei echter Änderung.** Sonst liefe bei eingeschaltetem
  Prüftakt alle paar Sekunden ein Schreibvorgang in die SharedPreferences.

#### Der Speicher hat keinen festen Schlüssel

Beide Merkmale können sich ändern. Zwei Einträge gelten als derselbe, wenn sie
sich **Adresse oder Name** teilen (`sameEntry()`). Daran hängen drei Dinge:

- Der **„Gemerkt"-Hinweis** der Suchliste vergleicht beide Merkmale
  (`containsAny()`) — sonst legte man ein Gerät versehentlich zweimal an.
- Beim **Nachlernen** darf ein Eintrag einem zweiten sein Merkmal nicht
  wegnehmen (`isTaken()`).
- **Bestandseinträge** werden unverändert gelesen: `name` und `address` sind
  dieselben Schlüssel geblieben, neue Felder fallen über `optString`/`optBoolean`
  auf ihre Vorgaben zurück.

#### Die Zeitgrenze, die es nicht gibt

**`InetAddress` kennt keine Zeitgrenze.** Die 1,5 s / 2,5 s in
`ReachabilityCheck` decken nur die HTTP-Verbindung ab, nicht die Auflösung
davor. Ein zäher DNS-Server hält deshalb eine Zeile länger auf, als die
Konstanten vermuten lassen — jede Abfrage hängt aber an ihrem eigenen Thread,
also nur die eine.

### Die Adresse wird über eine Maske aus vier Feldern eingegeben

Statt eines Textfelds mit Formatprüfung hinterher: vier Zahlenfelder mit Punkten
dazwischen. Die falsche Form lässt sich damit gar nicht erst eintippen, und es
gibt keine Rechnerei mit Cursorpositionen.

Was die Maske kann (`wireAddressMask()`):

- Nach der dritten Ziffer springt der Fokus von selbst weiter.
- Ein Punkt springt ebenfalls weiter, **ohne das nächste Feld zu leeren**.
- Rückschritt in einem leeren Feld springt zurück.
- Eine ganze Adresse in ein Feld getippt wird auf die folgenden verteilt.

> **Der Fallstrick, der beim ersten Versuch zuschlug:** Nach der dritten Ziffer
> ist der Fokus schon gewandert. Der danach getippte Punkt landet im nächsten,
> noch leeren Feld. Springt er dort **erneut** weiter, bleibt ein Feld leer und
> die restlichen Ziffern rutschen zusammen. Ein Punkt in einem leeren Feld darf
> deshalb **nicht** weiterspringen.

Geprüft wird je Feld: eine Zahl von 0 bis 255, höchstens drei Ziffern, **keine
führende Null**. `010` ist zweideutig — manche Auflöser lesen es als Oktalzahl.

Nur IPv4, weil Suche und Erreichbarkeitstest nichts anderes kennen.

> **Gerade Anführungszeichen (U+0022) verschluckt Android in
> String-Ressourcen.** Dort gehören die typografischen Zeichen hin (U+201E und
> U+201C) oder ein maskiertes `\"`.

### Das Startsymbol wird aus einer Vorlage erzeugt

Quelle ist **`icon.png` im Projektstamm** (1254 × 1254, ohne Alphakanal). Erzeugt
wird daraus mit `tools\make_icons.ps1`; das Skript findet Vorlage und Zielordner
über `$PSScriptRoot` und übersteht damit einen Umzug des Projektordners.

**Die Vorlage ist so nicht verwendbar:** Seit Android 8 legt das System seine
eigene Maske über das Symbol. Ein Bild, das seine Form schon mitbringt, ergibt
eine Form in der Form.

Das Skript baut deshalb zwei Ebenen — einen einfarbigen Hintergrund im **exakten
Ton des Quadrats der Vorlage** und einen Vordergrund mit dem freigestellten
Motiv auf **70 %**. Der Kniff steckt in der Farbgleichheit: Kanten und Reste der
Kantenglättung verschwinden darin.

**Die 70 % sind ausgemessen, nicht geschätzt.** Der am weitesten außen liegende
Punkt des Motivs sitzt rund 46 % der Kantenlänge vom Mittelpunkt entfernt, die
runde Maske lässt aber nur 33 % zu. Bei 80 % war das Motiv am Gerät sichtbar
angeschnitten.

Zwei Feinheiten, die sonst Ärger machen:

- Der Quellausschnitt ist **6 px nach innen gerückt** — genau auf der Kante
  liegen Mischpixel, die sonst als heller Saum stehen bleiben.
- Die einfarbige Silhouette (`ic_launcher_monochrome`) entsteht über eine
  Helligkeitsschwelle von 380 (Summe aus R+G+B).

> **Beim runden klassischen Symbol muss die Kreismaske zuletzt kommen.** Die
> Zeichenroutine setzt für das abgerundete Quadrat ihre eigene Beschneidung und
> hebt sie danach auf; ein vorher gesetzter Kreis ist dann wirkungslos. Lint
> findet das zuverlässig (`IconLauncherShape`).

> **Das Skript bricht ab, wenn die Vorlage fehlt.** Ohne diese Prüfung schreibt
> es klaglos leere Dateien, der Build läuft durch, und die Symbole sind zerstört,
> ohne dass etwas auffällt. Genau so ist es einmal passiert.

### Der Info-Bildschirm

| Abschnitt | Inhalt |
| --- | --- |
| **Über diese App** | wozu sie da ist, und dass auf den Geräten **Builty's Controller** läuft — ein eigenständiges Projekt, zu dem diese App **nicht** gehört |
| Urheber | „© 2026 ft972" |
| Lizenz | **MIT-0** für den eigenen Code, mit ausdrücklicher Abgrenzung zu den Bibliotheken |
| Quelltext | die Adresse des Projekts, antippbar (`autoLink`) |
| Datenschutz | beschreibt das tatsächliche Verhalten |
| Berechtigungen | erklärt alle drei und sagt, dass **keine Standortberechtigung** verlangt wird |
| Verwendete Bibliotheken | AndroidX und Material, Apache 2.0 — **mit dem vollständigen Lizenztext**, erreichbar über einen Knopf |
| **Dank** | an Builty für die Firmware, mit der Adresse seines Profils — dieselbe wie in der README, siehe die Namensregel ganz oben |
| Marken | allgemein gehalten, ohne Namen — siehe die Namensregel ganz oben |
| Haftung | ausformuliert |

**Der Lizenztext liegt bei, weil Apache 2.0 das verlangt** (Abschnitt 4), sobald
etwas weitergegeben wird. Er steht als `res/raw/apache_2_0.txt` im APK und
**stammt aus den Bibliotheken selbst**: Sowohl `activity` als auch `core`
liefern ihn unter `META-INF` mit, byte-identisch. Das ist keine Abschrift.
Übersetzt wird er **nicht** — maßgeblich ist die englische Fassung.

> **Der Lizenztext darf nicht umgebrochen werden.** Einmal zerriss der Umbruch
> die zentrierten Überschriften; seither scrollt `activity_license.xml` in beide
> Richtungen.

**Die Lizenz ist MIT-0** (MIT No Attribution, `LICENSE` im Projektstamm): nutzen,
ändern, weitergeben ohne Einschränkung und ohne Namensnennung. Sie war kurz CC0
und wurde gewechselt, weil MIT-0 **für Software gemacht** und von der OSI
anerkannt ist — und weil **CC0 ein Verzicht auf das Urheberrecht ist, den das
deutsche Recht nicht kennt** (§ 29 Abs. 1 UrhG).

> **GitHub kennt MIT-0 nicht — das ist hinzunehmen, nicht zu beheben.** Die
> Plattform führt nur eine feste Liste von Lizenzen; MIT-0 ist nicht dabei. Beim
> Anlegen des Repositorys deshalb **keine** Lizenz im Auswahlfeld wählen und die
> vorhandene Datei mitschicken. **Nicht auf MIT ausweichen, nur damit das
> Etikett erscheint**: MIT verlangt die Namensnennung, MIT-0 nicht, und genau
> darum wurde es gewählt.

> **Eine Einschränkung steht im Text und muss stehen bleiben:** Die Lizenz deckt
> nur den selbst geschriebenen Teil. Das fertige APK enthält AndroidX und
> Material unter Apache 2.0; „die ganze App ist MIT-0" wäre falsch.

Der Haftungstext schließt nicht pauschal alles aus, sondern „soweit gesetzlich
zulässig", und nimmt Vorsatz, grobe Fahrlässigkeit sowie Leben, Körper und
Gesundheit ausdrücklich aus.

> **Wer am Verhalten der App etwas ändert, ändert diese Texte mit.** Das ist
> keine Floskel: Eine Zeit lang stand dort, die App könne ein Gerät zum
> Umschalten auffordern — heute kann sie das nicht mehr, und der Satz wäre
> falsch geworden. Beide Male, hin wie zurück, war die Textänderung der Teil,
> den man beinahe übersehen hätte.

> **Das ist keine Rechtsberatung.** Sämtliche Texte im Info-Bildschirm sind von
> der KI formuliert und von niemandem mit Sachkenntnis geprüft. Für den
> Eigengebrauch tragfähig; vor der Veröffentlichung gehört das angesehen —
> offener Punkt 2.

Die Version im Kopf kommt aus dem **Paketmanager**, nicht aus `BuildConfig` — so
bleibt dessen Erzeugung abgeschaltet.

### Von der Datensicherung ausgenommen

**Die App wird nicht gesichert** — weder in ein Cloud-Backup noch bei einer
Übertragung auf ein neues Gerät.

**Der Anlass war ein Widerspruch, kein Wunsch.** Der Info-Bildschirm sagte zu,
die Favoriten blieben „lokal auf dem Gerät". Tatsächlich stand im Manifest
`allowBackup="true"`, und die beiden Regeldateien waren noch die **leeren
Vorlagen der Projektschablone**.

| | |
| --- | --- |
| **Der Preis** | Bei Gerätewechsel oder Neuinstallation sind die Favoriten weg. Eine Aktualisierung per `install -r` lässt sie unberührt |
| **Warum vertretbar** | Ein Favorit ist ein Name und eine IP-Adresse. Neu angelegt ist er in einer Minute |

**Es braucht drei Angaben im Manifest, nicht eine:**

| Angabe | Geltungsbereich |
| --- | --- |
| `allowBackup="false"` | die Gesamtaussage |
| `fullBackupContent="@xml/backup_rules"` | bis Android 11 |
| `dataExtractionRules="@xml/data_extraction_rules"` | ab Android 12, wo `allowBackup` abgekündigt ist |

> **Lint erzwingt genau diese Kombination**, und zwar in zwei Schritten. Wer hier
> etwas ändert, lässt Lint laufen und vergleicht mit 0 Fehlern / 2 Warnungen.

> **Das hängt am Info-Bildschirm.** Wer das Backup je wieder einschaltet, muss
> zuerst den Datenschutzabschnitt anfassen. Der Hinweis steht als Kommentar in
> beiden XML-Dateien, damit er dort gefunden wird, wo die Änderung stattfände.

### Die Release-Signatur

| | |
| --- | --- |
| Schlüssel | `.patchmanul/patchmanul-ft972.jks` im Benutzerverzeichnis |
| Zugangsdaten | `.patchmanul/keystore.properties` ebenda |
| Alias | `patchmanul` |
| Verfahren | RSA 4096, gültig 10 000 Tage |
| Zertifikat | `CN=ft972` — **kein Klarname, kein Ort** |
| Schema | **v2 und v3** (`enableV3Signing = true`) |
| Fertige APKs | `release/` im Projektordner, eine Datei je Fassung |

**Das Zertifikat ist aus jedem APK auslesbar**, und genau deshalb trägt es nur
das Pseudonym: Der ursprüngliche Schlüssel trug den bürgerlichen Namen und hätte
es mit einem Befehl aufgelöst. Der Wechsel fand am 20.08.2026 statt — **vor der
ersten Veröffentlichung**, denn ein Signaturwechsel bricht die
Aktualisierungskette und hätte danach jeden Nutzer zur Deinstallation gezwungen.

**v3 hält den einzigen Ausweg offen**, der bei einem kompromittierten Schlüssel
bleibt: ab Android 9 auf einen neuen zu wechseln. v2 bleibt daneben bestehen.

> **Beides liegt außerhalb des Projektordners, und das ist Absicht.** So wandern
> Schlüssel und Passwörter bei keinem Kopieren, Packen oder Einchecken mit.
> `app/build.gradle.kts` liest die Datei über `System.getProperty("user.home")`.

**Fehlt die Datei, wird unsigniert gebaut statt abgebrochen.** Auf einem Rechner
ohne den Schlüssel soll wenigstens `assembleDebug` weiterlaufen. Erkennbar am
Dateinamen: `app-release-unsigned.apk`.

> **Der Schlüssel wurde vom Nutzer erzeugt, nicht von der KI.** Zugangsdaten legt
> sie nicht an. Wird er je neu gebraucht: `keytool -genkeypair` in einer
> **eigenen Konsole**, nicht mit `-storepass` in der Befehlszeile — das landete
> in der Verlaufsdatei der Shell.

Drei Dinge, die daran hängen:

- **Der Schlüssel ist unersetzlich.** Geht er verloren, lässt sich die App nie
  wieder aktualisieren. Der Ordner gehört gesichert, und zwar woanders hin als
  auf dieselbe Platte.
- **Debug und Release schließen einander aus.** Verschiedene Schlüssel heißen
  `INSTALL_FAILED_UPDATE_INCOMPATIBLE`; ein Wechsel verlangt Deinstallation, und
  dabei sind die Favoriten weg.
- **`versionCode` muss vor jeder Aktualisierung steigen.**

### Versionszahl und CHANGELOG.md — von der KI zu führen

**Ausdrücklich Aufgabe der KI**, nicht des Nutzers. Wer hier weiterarbeitet,
pflegt beides mit, ohne dass danach gefragt wird.

`versionCode` steigt bei **jeder ausgelieferten Fassung** um eins. Ausnahmslos.

`versionName` ist `MAJOR.MINOR.PATCH`:

| Stufe | Wann |
| --- | --- |
| MAJOR | Umbau, der bisherige Bedienung oder gespeicherte Daten bricht |
| MINOR | eine neue Funktion |
| PATCH | Fehlerbehebung, Kleinigkeit, oder eine Änderung nur an der Auslieferung |

**`CHANGELOG.md`** führt die Fassungen, neueste zuerst. Er ist für den Nutzer
geschrieben: **was sich für ihn ändert**, nicht welche Datei angefasst wurde.
Diese Übergabe begründet Entscheidungen — der Changelog erzählt die Geschichte.
Beides gehört nicht vermischt.

> **Eine Fassung, die das Haus verlassen hat, ist verbraucht** — und sei es nur
> aufs Testgerät. Dann wird nicht dieselbe Nummer neu gebaut, sondern
> hochgezählt. **Die Lehre aus der Vorgeschichte: erst am Gerät ansehen, dann
> nach `release/` legen.** Zweimal ist genau das schiefgegangen.

### `Proxy.NO_PROXY` bei jeder Anfrage

Ein am WLAN eingetragener Proxy würde die Anfrage aus dem lokalen Netz heraus
schicken, wo das Gerät nicht erreichbar ist.

---

## Bauen, installieren, prüfen

**Gebaut wird in Android Studio** — es ist ein gewöhnliches Gradle-Projekt.

| | |
| --- | --- |
| **Bauen** | `Build > Build Bundle(s) / APK(s) > Build APK(s)` |
| **Variante umstellen** | Fenster `Build Variants` (links unten). **Vorgabe ist `debug`** |
| **Signatur** | steht im Buildscript — bei der Variante `release` kommt gleich ein signiertes APK heraus. `Generate Signed App Bundle / APK…` wird **nicht** gebraucht |

Die APKs landen unter `app\build\outputs\apk\<variante>\`. Daneben steht jeweils
eine `output-metadata.json` mit `versionName` und `versionCode` — der
verlässliche Weg festzustellen, was da wirklich liegt.

Zwei Fallen:

- **`Generate Signed App Bundle / APK…` schreibt woandershin.** Der Dialog
  schlägt `app\release\` vor — das ist ein Unterordner von `app\`, **nicht** der
  `release\`-Ordner des Projekts.
- **Nicht gleichzeitig aus Studio und von der Kommandozeile bauen.** Beide
  arbeiten im selben Gradle-Verzeichnis und warten dann aufeinander.

> **Nach `release/` kommt das APK von Hand**, unter dem Namen
> `PatchManul-<Version>.apk`. Vorher nachsehen, ob dort schon etwas unter dem
> Namen liegt.

Von der Kommandozeile (PowerShell) geht es ebenso. **Es ist kein separates JDK
installiert**; das einzige liegt in Android Studio, und `JAVA_HOME` muss bei
**jedem** Aufruf mitgegeben werden — der Shell-Zustand überlebt einzelne Aufrufe
nicht:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
& '<Projektordner>\gradlew.bat' -p '<Projektordner>' assembleDebug lintDebug
```

Für die Release-Fassung dasselbe mit `assembleRelease`. `apksigner verify -v
--print-certs` liest danach das Zertifikat **aus dem APK** und braucht dafür kein
Passwort — auch der Weg, um zu prüfen, was auf dem Handy wirklich installiert
ist (`adb shell pm path`, dann `adb pull`).

Dauer auf diesem Rechner: inkrementell unter einer Minute, `clean assembleDebug`
rund 45 Sekunden.

### Am Gerät prüfen

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb shell am start -W -n io.github.ft972.patchmanul/.MainActivity
& $adb shell input keyevent KEYCODE_WAKEUP     # zwingend vor jedem Tippen
& $adb shell input tap <x> <y>
& $adb shell screencap -p /sdcard/s.png ; & $adb pull /sdcard/s.png . ; & $adb shell rm -f /sdcard/s.png
```

Genaue Tippkoordinaten statt Raten:

```powershell
& $adb shell uiautomator dump /sdcard/ui.xml   # liefert bounds je Element
```

**Dateien nach dem Prüfen vom Handy löschen.**

**Menüs und Dialoge liest man aus dem Baum, statt zu raten.** Der Dump liefert
Text **und** `bounds` je Element; daraus ergibt sich beides — was im Menü steht
und wohin zu tippen ist.

**Eine rote Zeile herbeiführen, ohne etwas zu verstellen:**

```powershell
& $adb shell svc wifi disable    # ~20 s warten, dann prüft die App neu
& $adb shell svc wifi enable     # danach wieder verbinden lassen
```

Das ist das Mittel der Wahl für Zweige, die einen **nicht erreichbaren** Zustand
brauchen. Es verändert keine Daten der App und ist vollständig umkehrbar;
**hinterher nachsehen, ob das WLAN wirklich wieder steht**
(`adb shell ip -4 addr show wlan0`).

### Das Gerät direkt vom Rechner abfragen

**Wenn der Rechner im selben Subnetz hängt wie die Geräte** — das ist nicht immer
so —, lässt sich ohne den Umweg über das Handy nachsehen, was die App zu sehen
bekommt. Für die Erkennung genügt das hier:

```powershell
$req = [System.Net.HttpWebRequest]::Create('http://<adresse>/')
$req.Proxy = $null          # wie in der App: kein Proxy ins lokale Netz
$req.Timeout = 4000
$resp = $req.GetResponse()
$html = (New-Object System.IO.StreamReader($resp.GetResponseStream())).ReadToEnd()
$resp.Close()
$html.Substring(0, 2000)    # hier müssen beide Merkmale stehen
```

`Invoke-RestMethod` geht auch, kennt in Windows PowerShell 5.1 aber kein
`-NoProxy`.

Was das **nicht** ersetzt: die App selbst. Subnetzermittlung, Berechtigungen,
WebView und Oberfläche lassen sich nur am Handy prüfen.

### Ein Gerät nachbauen

**Ein kleiner HTTP-Server auf dem Rechner gibt sich als Gerät aus** — das Handy
findet ihn beim Suchlauf wie jedes andere. Viel gehört nicht dazu: Es genügt,
unter `/` eine Seite auszuliefern, in der die beiden Merkmale vorkommen.

Zwei Dinge, die das erst möglich machen:

- **`System.Net.Sockets.TcpListener` auf Port 80, nicht `HttpListener`.** Die App
  spricht fest Port 80 an, und `HttpListener` verlangt für alles außer localhost
  eine Reservierung mit Administratorrechten.
- **Die Antwort muss `Content-Type: text/html` tragen** — sonst bricht die App
  vor dem Lesen ab.

> **Was er nicht kann: beweisen, dass die echte Firmware sich so verhält.** Er
> ist nach demselben Verständnis gebaut wie die App — ein Lesefehler steckt dann
> in beiden. Er prüft den Ablauf der App, nicht die Annahmen darüber. **Ein
> Durchlauf am echten Gerät bleibt nötig.**

---

## Was am Gerät belegt ist — und was nicht

> **Der Stand in einem Satz: Die Erkennung ist am echten Gerät belegt.** Alles
> Übrige stammt aus der Vorgeschichte und ist von der Umstellung unberührt.

| Belegt am Original-Controller (20.08.2026) | |
| --- | --- |
| **Der Suchlauf findet das Gerät** | 253 Adressen im eigenen /24 durchsucht, **ein** Gerät gefunden — mit der neuen Erkennung über die Startseite. Der Lauf war in wenigen Sekunden durch, trotz der viel größeren Antwort |
| **Es ist der Original-Controller** | Auf demselben Gerät beantwortet `/api/ota/info` die Anfrage mit **404**. Die Vorgängerfassung hätte es nicht gefunden — genau das war der Anlass dieser Fassung |
| **Die Antwort sieht aus wie angenommen** | `GET /` → **200**, `Content-Type: text/html`, rund 138 KB. **Beide Merkmale kommen je einmal vor, und beide in den ersten 8 KB** — also innerhalb dessen, was die App überhaupt liest |
| **Der Erreichbarkeitstest** | Die Favoritenzeile steht auf `<adresse> · erreichbar`, die Fundstelle auf `erreichbar · gemerkt` |
| **Das Favoritenmenü** | zeigt **„Bearbeiten \| Entfernen"** — kein Umschalten, keine Merkhilfe |
| **Die Suchzeile ohne Menü** | Das Gerät ist gemerkt, also bleibt für das Menü nichts übrig — das Drei-Punkte-Symbol **erscheint gar nicht erst** |
| **Die Webansicht** | lädt die Geräteseite vollständig (ihre Navigation steht im Baum), **ohne Umschaltleiste**. Der Home-Knopf führt zurück zur Liste |
| **Die neue Paketkennung** | Der **Release**-Build unter `io.github.ft972.patchmanul` installiert sich neben der alten Fassung, startet (Kaltstart 408 ms) und findet das Gerät im Suchlauf ebenso. In den Paket-Flags **kein `DEBUGGABLE`**; Signatur `CN=ft972`, v2 und v3. Die Favoritenliste war erwartungsgemäß **leer** — eine andere Kennung heißt eigene Daten |

Alles Weitere darunter stammt aus früheren Prüfungen:

| Belegt — und von der Umstellung unberührt | |
| --- | --- |
| Suchlauf über 253 Adressen | Ablauf, Fortschrittsbalken („208 von 253 Adressen"), gesperrter Suchknopf |
| Favorit grün / rot | erreichbar bzw. tote Adresse → „nicht erreichbar" |
| Übernahme aus der Suche | Dialog vorbelegt, danach „gemerkt" in der Zeile |
| Adressmaske | eine ganze Adresse ins **erste** Feld getippt landet richtig verteilt; `999` wird abgewiesen, das falsche Feld markiert |
| Namensvorschlag aus DNS | Rückwärtsauflösung liefert den Namen |
| Weboberfläche | lädt in der WebView |
| Zurück-Taste | blättert in der Seite, verlässt dann den Bildschirm |
| Home-Knopf | aus einer Seite ohne Seitenverlauf sofort zurück zur Liste |
| Neustart der App | Favoriten überleben, werden beim Start geprüft |
| Bearbeiten und Entfernen | Dialog vorbelegt, **genau ein** Eintrag danach, keine Karteileiche |
| Einstellungen als eigene Seite | öffnet, vier Auswahlfelder; **im Querformat bringt ein Wisch die unteren ins Bild** — genau das, was der Dialog nicht konnte |
| Die Wahl hält | 900 ms gewählt, `am force-stop`, neu gestartet → steht noch |
| Sofort statt Speichern | keine Knöpfe mehr; Zurück-Taste genügt, der Wert steht |
| Sprachumschaltung | auf Englisch und zurück, die ganze Oberfläche wechselt sofort |
| Der Rückfall am Gerät vorgeführt | App-Sprache **Français**: Oberfläche französisch, **Info-Bildschirm englisch** |
| Design-Umschaltung | „Hell" wirkt sofort, übersteht `am force-stop`, zurück auf System trifft wieder den Systemmodus |
| Material You | feste Akzentfarbe durch die Systemfarbe ersetzt, Logcat sauber |
| Dialog „Nicht erreichbar" | genau zwei Knöpfe; „Erneut prüfen" stößt die Prüfung an und öffnet **keine** Webansicht |
| Fundstellen werden rot | WLAN am Handy abgeschaltet → beide Listen rot, danach wieder grün |
| Der stille Rückfall trägt | Favorit mit unauflösbarem Namen wurde **grün über die hinterlegte IP** |
| Die festgelegte Wahl bleibt | nach Neustart weiter „DNS-Name / Von dir festgelegt", die Automatik hat nicht überschrieben |
| Release-Fassung | signiert gebaut, installiert, gestartet; zurückgeholtes APK trägt denselben Hash, `CN=ft972`, kein `DEBUGGABLE`, Kaltstart 256 ms |
| Aktualisierung ohne Datenverlust | mehrfach `install -r` ohne Deinstallation, Favoriten überlebten |
| Info- und Lizenzbildschirm | durchgescrollt, alle Abschnitte da; der Apache-Text vollständig und seitlich schiebbar |
| `InfoActivity` nicht von außen startbar | `am start` scheitert mit `SecurityException: not exported` |

| **Nicht** geprüft | Warum |
| --- | --- |
| Ob das Lesen der größeren Startseite den Suchlauf verlangsamt | Belegt ist nur, dass ein Lauf über 253 Adressen weiterhin in Sekunden durch ist. **Gestoppt wurde nichts**, und ein Vergleich gegen die alte Fassung ist nicht zu haben |
| Die Erkennung an einer **anderen Firmware-Fassung** | Geprüft ist genau ein Gerät mit einer Fassung, die `/api/ota/info` nicht kennt. Ob ältere oder neuere Fassungen dieselbe Startseite ausliefern, ist offen — daran hängt die ganze Erkennung |
| Die **alte Installation** | Unter der alten Kennung liegt weiterhin eine Debug-Fassung auf dem Testgerät. Sie stört nicht — beide Kennungen leben nebeneinander —, aber sie ist auch nicht abgeräumt, und das ist die Entscheidung des Nutzers: Beim Deinstallieren sind ihre Favoriten weg |
| `ACCESS_LOCAL_NETWORK` | greift erst ab Android 17, das Testgerät läuft auf 15 |
| Mehrere Geräte gleichzeitig | es gibt nur eines |
| **Die Warnung vor einem großen Netz** | das Testnetz ist ein /24, die Rückfrage kommt dort gar nicht — der ganze Zweig samt Dauerschätzung ist ungeprüft |
| Der wiederkehrende Prüftakt | eingebaut, aber nicht über mehrere Takte beobachtet |
| Französisch und Spanisch | niemand hat sie gegengelesen |
| **Der Namensweg selbst** | Das Testnetz hat keinen Reverse-DNS. Ungeprüft sind: eine erfolgreiche Gegenprobe, ein über den Namen erreichtes Gerät, das Nachtragen der IP dahinter, das Nachlernen und die Automatik in Richtung Name |
| `dropAmbiguousNames()` | braucht zwei Geräte mit gleichem Namen im selben Suchlauf |
| **Ob die höhere Zeitgrenze wirklich mehr findet** | die eigentliche Frage, und sie ist offen. Dass der Wert durchschlägt, folgt aus dem Code; gemessen ist es nicht |
| Bearbeiten mit **geänderter Adresse** | der Zweig ist gebaut, aber nur mit gleichbleibender Adresse durchgespielt |
| Die Sortierung der Suchliste | es wird immer nur ein Gerät gefunden |
| Dass wirklich nichts gesichert wird | belegt ist nur, dass Manifest und Regeldateien es sagen. Wer es prüfen will: Favoriten anlegen, Backup auslösen, App löschen, neu installieren — danach muss die Liste leer sein |

---

## Umgebung

| | |
| --- | --- |
| Rechner | Windows 11, CPU von 2013, **14 GB RAM** |
| Android Studio | JBR = OpenJDK 25, **kein separates JDK installiert** |
| SDK | `%LOCALAPPDATA%\Android\Sdk`, Platform API 37, Build-Tools 36.0.0 |
| AGP / Gradle | 9.3.1 / 9.5.0 |
| Kotlin | **ohne eigenes Plugin** — AGP 9 bringt die Unterstützung mit |
| Testgerät | ein Android-15-Gerät (API 35, arm64) mit LineageOS |
| Emulator | **installiert, aber ohne virtuelles Gerät.** `-list-avds` liefert nichts. Wer einen braucht, lädt erst ein Systemabbild über `sdkmanager`. **Gebraucht wurde er nie** — alle Prüfungen liefen am echten Gerät |
| Netz | **wechselt oft — nicht als Konstante behandeln.** Der Rechner hängt regelmäßig *nicht* im selben Subnetz wie Handy und Gerät; ein Nachbau vom Rechner aus ist dann nicht erreichbar |
| Quelltext der Firmware | liegt als entpacktes Release neben dem Projekt. Fassung **2.0.4.2**, gegen die die Erkennung geschrieben ist |

Die Bibliotheken: `core-ktx` 1.19.0, `appcompat` 1.7.1, `material` 1.14.0,
`activity-ktx` 1.13.0, `androidx-junit` 1.3.0, `espresso-core` 3.7.0.
**`constraintlayout` wurde entfernt** — es wird nirgends benutzt.

---

## Fallstricke

- **Lange Textblöcke lassen sich nicht in einem Rutsch über die Shell
  schreiben.** Ab etwa 9 KB bricht die Zeile mit
  `unexpected EOF while looking for matching` ab — der Befehl wird abgeschnitten
  und das Zitat bleibt offen. Große Dateien in Abschnitten anhängen (`>>`),
  oder ein Skript schreiben und dieses ausführen.
- **Textersetzungen mit Backslash-Mustern scheitern in der Bash still.** Die
  Argumente gehen über die Windows-Kommandozeile an `sed.exe`/`perl.exe`, und
  dabei geht das Quoting verloren: `perl -pe 's/x/\x27/'` schrieb wörtlich
  `\x27` in die Datei — **ohne Fehlermeldung und mit Rückgabewert 0.** Sobald
  ein Backslash im Spiel ist, wird die Stelle neu geschrieben statt ersetzt;
  ein kurzes Python-Skript mit `str.replace` und einer Zählprüfung ist der
  verlässlichste Weg. **Danach nachsehen, ob wirklich drinsteht, was
  drinstehen soll.**
- **Windows beantwortet die Rückwärtsauflösung der eigenen Adresse selbst** —
  mit dem eigenen Rechnernamen. Das sieht nach einem funktionierenden DNS aus
  und ist keiner. **Wer die Namensauflösung im Netz prüft, nimmt dafür eine
  fremde Adresse**, nicht die eigene.
- **Ein gesperrter Bildschirm verschluckt `am start`.** `KEYCODE_WAKEUP` weckt
  den Bildschirm, holt die App aber nicht nach vorn. **Vor jedem Tippen mit
  `dumpsys activity activities | grep topResumedActivity` prüfen, was wirklich
  vorn ist.**
- **Der Handybildschirm schaltet sich zwischen ADB-Befehlen ab.** `screencap`
  liefert dann trotzdem ein Bild, `input tap` geht aber ins Leere — der Fehler
  sieht aus, als reagiere die App nicht.
- **Beim Prüfen über ADB nicht blind Screenshots ziehen.** Ein Wisch vom unteren
  Rand holt bei Gestensteuerung die zuletzt benutzte App zurück; hier ist so
  einmal der Posteingang des Nutzers im Bild gelandet.
- **Ein hastiger Suchlauf vom Rechner aus beweist gar nichts.** Einmal wurden
  alle 254 Adressen gleichzeitig angesprochen und nach **1,2 Sekunden**
  ausgewertet — Ergebnis: nichts, und daraus wurde geschlossen, das Gerät sei
  nicht im Netz. **Es war da.** Wer so misst, gibt dem ESP32-S3 mehr Zeit oder
  fragt die Adresse gezielt ab — „gefunden" ist ein Beleg, „nichts gefunden"
  nicht.
- **Favoriten hängen an der IP-Adresse oder am DNS-Namen**, mit dem jeweils
  anderen Merkmal als Rückfall. Beide können veralten. Ein roter Favorit heißt
  in der Regel: neu suchen.
- **Der Nutzer programmiert nicht selbst** und kann fehlerhaften Code nicht als
  solchen erkennen. Nichts raten, nichts erfinden: Endpunkte, Feldnamen und
  Verhalten vor der Verwendung im Quelltext nachschlagen. Ungeprüftes
  ausdrücklich als ungeprüft kennzeichnen.

---

## Offene Punkte

1. **Erledigt am 20.08.2026: Die Erkennung ist am echten Gerät belegt.** Der
   Suchlauf findet den Original-Controller, und `/api/ota/info` antwortet dort
   mit 404. Siehe „Was am Gerät belegt ist".

   > **Ein einzelner Fund beweist trotzdem nicht alles.** Ob ein Gerät gefunden
   > wird, hängt auch daran, wie beschäftigt es gerade ist — und geprüft ist
   > genau **eine** Firmware-Fassung. Findet die App eines Tages nichts mehr,
   > sind die beiden Merkmale in `BoardProbe.MARKERS` die erste Stelle zum
   > Nachsehen; was die Startseite wirklich enthält, zeigt der Abruf unter
   > „Das Gerät direkt vom Rechner abfragen" — oder `curl` auf dem Handy
   > selbst, das ist der kürzere Weg und hat hier funktioniert.

2. **Was für die Veröffentlichung noch fehlt.** Info-Bildschirm, README und
   Lizenzdatei sind fertig. **Offen bleibt:**

   - **Dass jemand mit Sachkenntnis die Texte ansieht.** Lizenz, Datenschutz
     und Haftung sind von der KI formuliert. Das ist der Punkt, der vor einer
     Veröffentlichung wirklich zählt.
   - **Die Impressumsfrage** — ob und wann davon etwas nötig ist, wenn eine
     unentgeltliche App auf GitHub liegt, ist ungeklärt.
   - **Beim Anlegen des Repositorys keine Lizenz im Auswahlfeld wählen**,
     sondern die vorhandene `LICENSE` mitschicken.
   - **Erledigt am 20.08.2026: die Paketkennung.** Sie trug eine
     private Domäne und heißt jetzt `io.github.ft972.patchmanul`. Das war der
     letzte Punkt, der vor der Veröffentlichung fallen musste — danach wäre
     jede Änderung für Android eine andere App gewesen.
   - **Die Namensregel gilt auch dort**: Repo-Name, Beschreibung und
     Screenshots dürfen die Marke nicht führen. Die eine erlaubte Ausnahme ist
     die Adresse des Firmware-Projekts in der README.

3. **Den Ordner `.patchmanul` gesichert halten.** Keine Aufgabe, sondern eine
   Pflicht: Ohne den Release-Schlüssel lässt sich die App nie wieder
   aktualisieren, und eine Kopie auf derselben Platte ist keine Sicherung.

4. **Den Namensweg in einem Netz mit Rückwärtszone prüfen.** Ohne Reverse-DNS
   ist die halbe Funktion nur am Code belegt: die erfolgreiche Gegenprobe, ein
   über den Namen erreichtes Gerät, das Nachtragen der IP, das Nachlernen und
   die Automatik in Richtung Name. **In einem Netz mit gepflegter
   Rückwärtszone wäre das in Minuten erledigt** — Suchlauf, dann steht im
   Merken-Dialog ein Name statt einer Adresse und der Schalter auf „DNS-Name".

5. **Die Zeitgrenze der Suche am echten Netz erproben.** Die Einstellung ist am
   Gerät belegt, **ihre Wirkung nicht**: Ob ein Gerät, das bei 600 ms
   durchrutscht, bei 800 oder 900 ms gefunden wird, ist offen — und um wie viel
   der Lauf dabei länger dauert, ebenso. Dazu kommt die Frage, ob das Lesen
   der großen Startseite daran etwas ändert.

6. **Der UDP-Locater auf Port 12106.** Die Firmware sendet alle drei Sekunden
   ihre IP-Adresse als XML ins Netz. Als **Ergänzung** könnte das den Suchlauf
   beschleunigen — zuhören statt 253 Adressen abklappern —, als Ersatz taugt es
   nicht: Broadcasts werden in manchen Netzen gefiltert, und im Paket steht nur
   die Adresse, kein Name. Ein zweiter Weg neben dem Sweep, kein Austausch.

7. **mDNS als zweiter Weg.** Die Firmware betreibt einen Responder mit
   einstellbarem Namen. Android erreicht ihn über `NsdManager` — nicht über
   `InetAddress`. Das wäre ein eigener Suchweg mit eigenem Code; ob er den
   Aufwand lohnt, hängt daran, wie gut der Sweep in der Praxis trägt (Punkt 1).

8. **Zoomfaktor für die Geräteseite** — möglich, aber sauberer wäre ein
   `viewport`-Meta-Tag in der Firmware. Die ist allerdings fremder Bestand.

9. **`androidx.appcompat` liegt eine Version zurück** (1.7.1 statt 1.8.0, von
   Lint gemeldet). Kein Fehler, keine Dringlichkeit — aber ein Update ist ein
   eigener, bewusster Schritt und keine Nebenwirkung einer Funktionsänderung.

10. **Die Warnung vor einem großen Netz ist nie ausgelöst worden**, weil das
    Testnetz ein /24 ist. Wer sie prüfen will, braucht ein WLAN mit kürzerem
    Präfix — oder ändert `NARROW_PREFIX` in `NetworkScanner` versuchsweise auf
    25, dann greift die Rückfrage auch im /24.

11. **Die Zeitgrenze der Namensauflösung nachmessen.** `InetAddress` bietet
    keine an; wie lange eine Zeile im schlechtesten Fall hängt, ist damit offen.
    Erst bei mehreren namensgeführten Favoriten in einem Netz mit trägem DNS von
    Belang — dann aber die erste Stelle zum Nachsehen.

12. **Der Erreichbarkeitstest hat feste Zeitgrenzen** (1,5 s / 2,5 s in
    `ReachabilityCheck`). Das ist Absicht — dort geht es um wenige bekannte
    Ziele. Wenn ein Favorit in einem trägen Netz aber grundlos rot wird, wäre
    das die nächste Stellschraube, und sie bräuchte einen eigenen
    Einstellungspunkt.
