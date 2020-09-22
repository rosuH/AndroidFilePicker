package me.rosuh.sample

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager
import java.util.*


const val LANG_ARABIC = "ar"
const val LANG_ENGLISH = "en"

object LocalizationHelper {

    private const val PREF_LOCALE = "PREF_LOCALE"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setLocale(context: Context, langCode: String): Context {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        val locale = Locale(langCode)
        Locale.setDefault(locale)

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    fun onAttach(context: Context): Context {
        val lang = getCurrentLocale(context)
        return setLocale(context, lang)
    }

    fun switchLocale(context: Context, langCode: String) {
        val sp = getSharedPreferences(context)
        sp.edit().putString(PREF_LOCALE, langCode).apply()
    }

    private fun getCurrentLocale(context: Context): String {
        val sp = getSharedPreferences(context)
        return sp.getString(PREF_LOCALE, /*Default Localization*/ LANG_ARABIC)!!
    }
}