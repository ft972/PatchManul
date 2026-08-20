# PatchManul

An Android app that finds ESP32-S3 pedal controllers on your Wi-Fi network and
opens their web interface — without typing IP addresses.

*[Deutsche Fassung weiter unten](#patchmanul-deutsch)*

---

The firmware on those devices is [Builty's Controller][controller], an
open-source project of its own. It already ships a full web interface; this app
is only the way to it. It scans the local network, remembers the devices you
care about, shows whether they are reachable, and opens the page in a WebView.

## What it does

- **Scan** the local network and list every controller that answers.
- **Keep favourites**, addressed by IP address *or* DNS name — whichever is
  stable on your network. The other one stays stored as a silent fallback and is
  filled in automatically, so a favourite survives a new DHCP lease.
- **Show reachability** with a green/red marker, on start and on every return,
  optionally on a repeating interval.
- **Open the device page** in a WebView, with a home button and a reload button.

## What it does not do

- It does not control the devices. Nothing is set, switched or flashed by this
  app — everything happens on the device's own page.
- It does not upload firmware.
- It sends nothing anywhere. Requests go to addresses on the local network only.

## Requirements

- Android 8.0 (API 26) or newer.
- The phone and the device on the **same Wi-Fi network**.
- On Android 17 and newer, the system asks for the *local network* permission.
  Without it the scan finds nothing.

## Install

Download the APK from [`release/`](release/) and install it. The app is signed
with a self-made key, so Android will ask you to allow installation from this
source.

## Build

A plain Gradle project — Android Studio's `Build > Build APK(s)` is enough. From
the command line:

```
gradlew assembleDebug
```

The `release` variant is signed if a `keystore.properties` file is present in
`.patchmanul` inside your home directory; without it, the release APK is built
unsigned instead of failing the build.

## How devices are recognised

The firmware's web server serves its interface under `/` and answers every other
path with 404 — there is no status endpoint to ask. A device is therefore
recognised by its **start page**: the app requests `http://<address>/` and looks
for two markers in the first few kilobytes. Anything else on port 80 — routers,
printers, NAS boxes — falls through.

The scan covers your own /24 range. On a larger network the app asks first,
because a full sweep can take minutes.

## Languages

English (default), German, French and Spanish. The info screen exists in English
and German only — it carries licence, privacy and liability statements, and an
unreviewed translation of those is worse than none.

## Privacy

Favourites are stored on the device and nowhere else. The app is excluded from
the system backup, so its data does not travel to a cloud backup either. There is
no analytics, no advertising and no crash reporting. The full statement is in the
app's info screen.

## Thanks

Thank you to **Builty** for [the controller firmware][controller] — a great piece
of work, developed in the open and shared freely. Without it this app would have
nothing to point at.

This app is a separate project. It is not part of that one, contains none of its
code, and is not endorsed by it.

PatchManul itself was written with [Claude Code][claude-code].

## Licence

MIT-0 (MIT No Attribution) — see [LICENSE](LICENSE). Use, modify and redistribute
it freely, commercially included, without attribution.

That covers the code written for this app. The bundled libraries (AndroidX,
Material Components for Android) are distributed under the Apache License 2.0;
its full text ships with the app and can be read from the info screen.

## Trademarks

Product and company names that appear in the web interfaces shown by this app, or
on the pages linked from here, are trademarks or registered trademarks of their
respective owners. Their use implies no connection with them.

---

<a name="patchmanul-deutsch"></a>

# PatchManul (deutsch)

Eine Android-App, die ESP32-S3-Pedal-Controller im WLAN findet und ihre
Weboberfläche öffnet — ohne dass man IP-Adressen eintippen muss.

Auf den Geräten läuft [Builty's Controller][controller], ein eigenständiges
quelloffenes Projekt. Es bringt bereits eine vollständige Weboberfläche mit;
diese App ist nur der Weg dorthin. Sie durchsucht das lokale Netz, merkt sich die
Geräte, die zählen, zeigt an, ob sie erreichbar sind, und öffnet ihre Seite in
einer WebView.

## Was sie kann

- **Suchen** im lokalen Netz und jeden Controller auflisten, der antwortet.
- **Favoriten merken**, angesprochen über die IP-Adresse *oder* den DNS-Namen —
  je nachdem, was im eigenen Netz stabil ist. Das jeweils andere Merkmal bleibt
  als stiller Rückfall gespeichert und wird selbsttätig nachgetragen; ein Favorit
  übersteht damit einen neuen DHCP-Lease.
- **Erreichbarkeit anzeigen** mit grüner und roter Markierung, beim Start und bei
  jeder Rückkehr, auf Wunsch in einem wiederkehrenden Takt.
- **Die Geräteseite öffnen** in einer WebView, mit Startseiten- und
  Neu-laden-Knopf.

## Was sie nicht tut

- Sie steuert die Geräte nicht. Diese App setzt, schaltet und spielt nichts auf —
  alles geschieht auf der Seite des Geräts selbst.
- Sie lädt keine Firmware hoch.
- Sie sendet nichts nach außen. Anfragen gehen ausschließlich an Adressen im
  lokalen Netz.

## Voraussetzungen

- Android 8.0 (API 26) oder neuer.
- Handy und Gerät im **selben WLAN**.
- Ab Android 17 fragt das System nach der Berechtigung für das *lokale Netz*.
  Ohne sie findet die Suche nichts.

## Installieren

Das APK aus [`release/`](release/) herunterladen und installieren. Die App ist
mit einem selbst erzeugten Schlüssel signiert; Android fragt deshalb nach, ob
aus dieser Quelle installiert werden darf.

## Bauen

Ein gewöhnliches Gradle-Projekt — `Build > Build APK(s)` in Android Studio
genügt. Auf der Kommandozeile:

```
gradlew assembleDebug
```

Die Variante `release` wird signiert, sofern im Benutzerverzeichnis unter
`.patchmanul` eine `keystore.properties` liegt; fehlt sie, entsteht ein
unsigniertes APK, statt dass der Build abbricht.

## Wie Geräte erkannt werden

Der Webserver der Firmware liefert seine Oberfläche unter `/` aus und beantwortet
jeden anderen Pfad mit 404 — es gibt keinen Status-Endpunkt, den man fragen
könnte. Ein Gerät wird deshalb an seiner **Startseite** erkannt: Die App fragt
`http://<adresse>/` ab und sucht in den ersten Kilobytes nach zwei Merkmalen.
Alles andere auf Port 80 — Router, Drucker, NAS — fällt durch.

Die Suche umfasst den eigenen /24-Bereich. In einem größeren Netz fragt die App
vorher nach, denn ein vollständiger Lauf kann Minuten dauern.

## Sprachen

Englisch (Grundfassung), Deutsch, Französisch und Spanisch. Den Info-Bildschirm
gibt es nur auf Englisch und Deutsch — dort stehen Lizenz-, Datenschutz- und
Haftungsaussagen, und eine ungeprüfte Übersetzung davon ist schlechter als keine.

## Datenschutz

Favoriten werden auf dem Gerät gespeichert und sonst nirgends. Die App ist von
der Datensicherung des Systems ausgenommen, ihre Daten wandern also auch nicht in
ein Cloud-Backup. Es gibt keine Nutzungsanalyse, keine Werbung und keine
Absturzberichte. Der vollständige Text steht im Info-Bildschirm der App.

## Dank

Dank an **Builty** für [die Controller-Firmware][controller] — großartige Arbeit,
offen entwickelt und frei geteilt. Ohne sie hätte diese App nichts, worauf sie
zeigen könnte.

Diese App ist ein eigenes Projekt. Sie gehört nicht zu jenem, enthält keinen
seiner Quelltexte und ist von ihm auch nicht befürwortet.

Geschrieben wurde PatchManul mit [Claude Code][claude-code].

## Lizenz

MIT-0 (MIT No Attribution) — siehe [LICENSE](LICENSE). Nutzen, ändern und
weitergeben ohne Einschränkung, auch gewerblich und ohne Namensnennung.

Das betrifft den für diese App geschriebenen Code. Die mitgelieferten
Bibliotheken (AndroidX, Material Components für Android) stehen unter der Apache
License 2.0; ihr vollständiger Text liegt der App bei und ist im Info-Bildschirm
zu lesen.

## Marken

Produkt- und Firmennamen, die in den angezeigten Weboberflächen oder auf den von
hier verlinkten Seiten vorkommen, sind Marken oder eingetragene Marken ihrer
jeweiligen Inhaber. Aus ihrer Nennung folgt keine Verbindung zu ihnen.

[controller]: https://github.com/Builty/
[claude-code]: https://claude.com/claude-code
