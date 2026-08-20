import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

/**
 * Die Zugangsdaten des Release-Schluessels stehen **ausserhalb** des
 * Projektordners, damit sie nicht versehentlich mitkopiert oder eingecheckt
 * werden. Erwartet wird:
 *
 *     <Benutzerverzeichnis>/.patchmanul/keystore.properties
 *         storeFile=<vollstaendiger Pfad zur .jks-Datei>
 *         storePassword=...
 *         keyAlias=patchmanul
 *         keyPassword=...
 *
 * **Fehlt die Datei, wird das Release-APK unsigniert gebaut** statt der Build
 * abzubrechen: Auf einem Rechner ohne den Schluessel soll wenigstens noch
 * assembleDebug laufen. Ein unsigniertes APK laesst sich nicht installieren -
 * das faellt beim Versuch sofort auf.
 */
val keystoreProperties = Properties().apply {
    val file = File(System.getProperty("user.home"), ".patchmanul/keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "io.github.ft972.patchmanul"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.github.ft972.patchmanul"
        minSdk = 26
        targetSdk = 37
        /*
         * versionCode zaehlt bei **jeder** ausgelieferten Fassung um eins hoch,
         * ausnahmslos - Android lehnt eine Installation sonst ab. Er hat nichts
         * mit versionName zu tun und wird nie angezeigt.
         *
         * versionName ist MAJOR.MINOR.PATCH und richtet sich nach der Groesse
         * der Aenderung:
         *   MAJOR  Umbau, der bisherige Bedienung oder Daten bricht
         *   MINOR  neue Funktion
         *   PATCH  Fehlerbehebung, Kleinigkeit, oder eine Aenderung wie hier,
         *          die nur die Auslieferung betrifft
         *
         * **Welche Fassung was gebracht hat, steht in CHANGELOG.md** - hier
         * nur die jeweils aktuelle:
         *
         * 1 / 1.0.0: Die erste veroeffentlichte Fassung.
         *
         * Die Zaehlung faengt hier an. Vor der Veroeffentlichung gab es eine
         * laengere Vorgeschichte unter anderer Paketkennung; sie ist mit der
         * Ausrichtung auf den Original-Controller hinfaellig geworden und wird
         * nicht fortgezaehlt. **Ab hier steigt versionCode wieder bei jeder
         * ausgelieferten Fassung.**
         */
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystoreProperties.isNotEmpty()) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")

                // v3 zusaetzlich zu v2. Es kostet nichts und haelt einen Ausweg
                // offen: Nur mit v3 laesst sich ab Android 9 auf einen neuen
                // Schluessel wechseln, falls der alte je abhanden kommt.
                // v2 bleibt daneben bestehen - aeltere Geraete pruefen damit
                // weiter, und weil dasselbe Zertifikat dahintersteht, gilt eine
                // so signierte Fassung als Aktualisierung der bisherigen.
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            // Null, solange die Datei mit den Zugangsdaten fehlt.
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}