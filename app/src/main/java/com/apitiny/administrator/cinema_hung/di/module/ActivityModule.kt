package com.apitiny.administrator.cinema_hung.di.module

import android.app.Activity
import com.apitiny.administrator.cinema_hung.ui.main.MainContract
import com.apitiny.administrator.cinema_hung.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter()
    }

}