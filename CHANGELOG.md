# Änderungen

Alle nennenswerten Änderungen an PatchManul, neueste zuerst.

Die Versionszahl folgt `MAJOR.MINOR.PATCH` und richtet sich nach der Größe der
Änderung — **MAJOR** bricht bisherige Bedienung oder Daten, **MINOR** bringt eine
neue Funktion, **PATCH** behebt etwas oder betrifft nur die Auslieferung. Der
`versionCode` daneben zählt bei jeder ausgelieferten Fassung um eins hoch; er
wird nie angezeigt und dient allein Android dazu, eine Aktualisierung als solche
zu erkennen.

---

## 1.0.0 — 20.08.2026

*versionCode 1 · [PatchManul-1.0.0.apk](release/PatchManul-1.0.0.apk)*

Die erste Fassung.

### Was die App kann

- **Geräte suchen.** Die App ermittelt das eigene Subnetz und fragt jede Adresse
  darin einmal ab. Treffer erscheinen sofort, nicht erst am Ende des Laufs. Ist
  das Netz größer als /24, fragt sie vorher nach — ein vollständiger Lauf kann
  dort Minuten dauern.

- **Favoriten merken.** Ein Favorit wird über seine **IP-Adresse** oder seinen
  **DNS-Namen** geführt. Das jeweils andere Merkmal bleibt gespeichert und
  springt ein, wenn das gewählte einmal nicht trägt; die App trägt es selbst
  nach. So übersteht ein Favorit, dass sich die Adresse ändert.

- **Erreichbarkeit anzeigen.** Beim Start und bei jeder Rückkehr wird geprüft,
  was gerade antwortet — grün, rot oder grau, solange die Prüfung läuft. Auf
  Wunsch auch in einem wiederkehrenden Takt, solange die App offen ist.

- **Die Geräteseite öffnen.** Sie erscheint in einer WebView, mit einem Knopf
  zurück zur Liste und einem zum Neuladen.

- **Einstellungen:** Prüftakt, Zeitgrenze der Suche (600–900 ms, für träge
  Netze), Design (Hell/Dunkel/System) und Sprache.

- **Vier Sprachen:** Englisch, Deutsch, Französisch, Spanisch. Der
  Info-Bildschirm steht auf Englisch und Deutsch.

### Was sie nicht tut

Sie steuert die Geräte nicht, spielt nichts auf und sendet nichts nach außen.
Anfragen gehen ausschließlich an Adressen im lokalen Netz.

### Datenschutz

Favoriten bleiben auf dem Gerät. Die App ist von der Datensicherung des Systems
ausgenommen, ihre Daten wandern also auch nicht in ein Cloud-Backup. Der Preis
dafür: Bei einem Gerätewechsel sind die Favoriten neu anzulegen.
