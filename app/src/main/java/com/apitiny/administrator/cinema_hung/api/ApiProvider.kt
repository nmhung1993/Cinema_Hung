package com.apitiny.administrator.cinema_hung.api

import android.util.Log
import com.apitiny.administrator.cinema_hung.Constants
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiProvider {
    private val TAG = "ApiProvider"

    private val mApiServiceNetwork = ApiServiceNetwork.getInstance()

    fun callApi(apiResult: ApiResult) {
        try {
            mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
                    .getFilmList()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<ListFilmResponse>() {
                        override fun onCompleted() {
                            //Do nothing for now
                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "onError" + Log.getStackTraceString(e))
                            apiResult.onAPIFail()
                        }

                        override fun onNext(getFilm: ListFilmResponse) {
                            Log.i(TAG, "Operation performed successfully")
                            apiResult.onModel(getFilm)
                        }
                    })
        } catch (e: Exception) {
            Log.e(TAG, "Exception" + Log.getStackTraceString(e))
            apiResult.onError(e)
        }

    }


}