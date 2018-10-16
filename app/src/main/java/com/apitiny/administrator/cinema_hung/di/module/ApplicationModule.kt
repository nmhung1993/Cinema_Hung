package com.apitiny.administrator.cinema_hung.di.module

import android.app.Application
import com.apitiny.administrator.cinema_hung.BaseApp
import com.apitiny.administrator.cinema_hung.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val baseApp: BaseApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideApplication(): Application {
        return baseApp
    }
}