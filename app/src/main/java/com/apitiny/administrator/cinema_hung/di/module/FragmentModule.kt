package com.apitiny.administrator.cinema_hung.di.module

import com.apitiny.administrator.cinema_hung.api.ApiServiceInterface
import com.apitiny.administrator.cinema_hung.ui.about.AboutContract
import com.apitiny.administrator.cinema_hung.ui.about.AboutPresenter
import com.apitiny.administrator.cinema_hung.ui.list.ListContract
import com.apitiny.administrator.cinema_hung.ui.list.ListPresenter
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {

    @Provides
    fun provideAboutPresenter(): AboutContract.Presenter {
        return AboutPresenter()
    }

    @Provides
    fun provideListPresenter(): ListContract.Presenter {
        return ListPresenter()
    }

    @Provides
    fun provideApiService(): ApiServiceInterface {
        return ApiServiceInterface.create()
    }
}