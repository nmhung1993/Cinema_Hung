package com.apitiny.administrator.cinema_hung

import android.app.Application
import com.apitiny.administrator.cinema_hung.di.component.ApplicationComponent
import com.apitiny.administrator.cinema_hung.di.component.DaggerApplicationComponent
import com.apitiny.administrator.cinema_hung.di.module.ApplicationModule

class BaseApp: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()

        if (BuildConfig.DEBUG) {
            // Maybe TimberPlant etc.
        }
    }

    fun setup() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this)).build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    companion object {
        lateinit var instance: BaseApp private set
    }
}