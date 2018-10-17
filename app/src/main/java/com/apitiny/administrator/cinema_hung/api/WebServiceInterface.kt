package com.apitiny.administrator.cinema_hung.api

import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import rx.Observable

interface WebServiceInterface {

    @GET("cinema")
    fun getFilmList(): Observable<ListFilmResponse>

    @Multipart
    @POST("cinema")
    fun postFilm(): Observable<FilmModel>

}
