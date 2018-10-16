package com.apitiny.administrator.cinema_hung.di.component

import com.apitiny.administrator.cinema_hung.di.module.FragmentModule
import com.apitiny.administrator.cinema_hung.ui.about.AboutFragment
import com.apitiny.administrator.cinema_hung.ui.list.ListFragment
import dagger.Component

@Component(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(aboutFragment: AboutFragment)

    fun inject(listFragment: ListFragment)

}