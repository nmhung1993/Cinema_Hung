package com.apitiny.administrator.cinema_hung

import android.content.Context
import android.preference.PreferenceManager
import android.R.id.edit
import android.content.SharedPreferences
import android.app.Application




class PreferencesHelper(context: Context){
    companion object {
        val DEVELOP_MODE = false
        private val DEVICE_TOKEN = "data.source.prefs.DEVICE_TOKEN"
    }

    fun saveVal(app: Application, key: String, value: String?) {
        val pref = PreferenceManager.getDefaultSharedPreferences(app)
        val prefEditor = pref.edit()
        prefEditor.putString(key, value).apply()
//        prefEditor.apply()
    }

    fun getVal(app: Application, key: String): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(app)
        return pref.getString(key, null)
    }

    fun delVal(app: Application, key: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(app)
        val prefEditor = pref.edit()
        prefEditor.remove(key).apply()
//        prefEditor.apply()
    }
}