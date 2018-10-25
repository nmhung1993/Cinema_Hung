package com.apitiny.administrator.hungcinema

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager


class PreferencesHelper(context: Context) {
  companion object {
    val DEVELOP_MODE = false
    private val DEVICE_TOKEN = "data.source.prefs.DEVICE_TOKEN"
  }
  
  fun saveVal(app: Application, key: String, value: String?) {
    val pref = PreferenceManager.getDefaultSharedPreferences(app)
    val prefEditor = pref.edit()
    prefEditor.putString(key, value).apply()
  }
  
  fun getVal(app: Application, key: String): String? {
    val pref = PreferenceManager.getDefaultSharedPreferences(app)
    return pref.getString(key, null)
  }
  
  fun delVal(app: Application, key: String) {
    val pref = PreferenceManager.getDefaultSharedPreferences(app)
    val prefEditor = pref.edit()
    prefEditor.remove(key).apply()
  }
}