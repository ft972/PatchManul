package io.github.ft972.patchmanul

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

/**
 * Wendet Farbmodus und Akzentfarbe an, bevor die erste Activity entsteht.
 *
 * Anders als bei der Sprache speichert AppCompatDelegate.setDefaultNightMode()
 * nichts von sich aus - ohne diese Stelle wuerde die App nach jedem
 * Prozessende auf den Systemmodus zurueckfallen, ganz gleich, was zuletzt
 * eingestellt war.
 *
 * DynamicColors.applyToActivitiesIfAvailable() ist auf Android unter 12 und auf
 * Systemen ohne Farbextraktion aus dem Hintergrundbild wirkungslos - dann bleibt
 * es beim festen Lila aus themes.xml. Keine eigene Pruefung noetig, das steckt
 * schon im "IfAvailable".
 */
class PatchManulApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(SettingsStore(this).themeMode)
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
