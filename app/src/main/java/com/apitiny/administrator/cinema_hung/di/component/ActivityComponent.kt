package com.apitiny.administrator.cinema_hung.di.component

import com.apitiny.administrator.cinema_hung.di.module.ActivityModule
import com.apitiny.administrator.cinema_hung.ui.main.MainActivity
import dagger.Component

/**
 * Created by ogulcan on 07/02/2018.
 */
@Component(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

}