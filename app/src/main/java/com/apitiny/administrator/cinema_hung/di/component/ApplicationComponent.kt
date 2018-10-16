package com.apitiny.administrator.cinema_hung.di.component

import com.apitiny.administrator.cinema_hung.BaseApp
import com.apitiny.administrator.cinema_hung.di.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(application: BaseApp)

}