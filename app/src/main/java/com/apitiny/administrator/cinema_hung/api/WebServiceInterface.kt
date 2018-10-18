package com.apitiny.administrator.cinema_hung.api

import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import retrofit2.http.POST



interface WebServiceInterface {

    @GET("cinema")
    fun getFilmList(): Observable<ListFilmResponse>

    @Multipart
    @POST("cinema")
    fun postFilm(
        @PartMap body: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part)
        : Observable<FilmModel>

//    @POST("/auth/signin")
//    fun login(@Body request: SigninRequest, callback: Callback<SigninResponse>)
}
