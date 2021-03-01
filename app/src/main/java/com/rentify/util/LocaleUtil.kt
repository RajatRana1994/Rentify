package com.rentify.util

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.DisplayMetrics
import java.util.*


class LocaleUtil {
    companion object{
    fun Context.setLocale(lang: String, intent: Intent? = null) {
        val myLocale = Locale(lang)
        val dm: DisplayMetrics = getResources().getDisplayMetrics()
        val conf: Configuration = getResources().getConfiguration()
        conf.locale = myLocale
        getResources().updateConfiguration(conf, dm)
        if (intent != null) startActivity(intent)
    }
    }
}