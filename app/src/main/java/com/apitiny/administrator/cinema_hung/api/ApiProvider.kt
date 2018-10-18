package com.apitiny.administrator.cinema_hung.api

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.Constants
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class ApiProvider {
    private val TAG = "ApiProvider"

    private val mApiServiceNetwork = ApiServiceNetwork.getInstance()

    fun callApiGet(apiResult: ApiResult) {
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

    fun callApiPost(apiResult: ApiResult, parseMap: HashMap<String,RequestBody>, file: MultipartBody.Part, context: Context) {
        try {
            mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
                    .postFilm(parseMap, file)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<FilmModel>() {
                        override fun onCompleted() {
                            Toast.makeText(context, "UploadActivity thành công!", Toast.LENGTH_SHORT).show()
                            // get activity để kết thúc UploadActivity Activity
                            val activity : Activity  = context as Activity
                            activity.finish()

                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "Lỗi" + Log.getStackTraceString(e))
                            apiResult.onAPIFail()
                        }

                        override fun onNext(postFilm: FilmModel) {
                            Log.i(TAG, "Operation performed successfully")
                            apiResult.onModel(postFilm)
                        }
                    })
        } catch (e: Exception) {
            Log.e(TAG, "Exception" + Log.getStackTraceString(e))
            apiResult.onError(e)
        }

    }

}