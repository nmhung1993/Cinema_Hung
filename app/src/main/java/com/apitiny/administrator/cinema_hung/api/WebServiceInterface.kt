package com.apitiny.administrator.cinema_hung.api

import com.apitiny.administrator.cinema_hung.model.*
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import retrofit2.http.POST



interface WebServiceInterface {

    @GET("cinema")
    fun getFilmList(): Observable<ListFilmResponse>

    @GET("cinema/{id}")
    fun getFilmDetail(
            @Path("id") id : String
    ): Observable<FilmDetailModel>

    @Multipart
    @POST("cinema")
    fun postFilm(
        @PartMap body: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part)
        : Observable<FilmModel>

    @FormUrlEncoded
    @POST("auth/signup")
    fun signup(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<ResponseModel>

    @FormUrlEncoded
    @POST("auth/signin")
    fun signin(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<ResponseModel>

    @FormUrlEncoded
    @POST("auth/reset-password")
    fun resetpw(
            @Field("email") email: String
    ): Observable<ResponseModel>

//    @POST("/auth/signin")
//    fun signin(@Body request: SigninRequest, callback: Callback<SigninResponse>)
}
