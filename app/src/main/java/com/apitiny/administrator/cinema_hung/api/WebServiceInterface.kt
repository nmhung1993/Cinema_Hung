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

    @FormUrlEncoded
    @POST("user/edit")
    fun postEditName(
            @Header("x-access-token") token:String,
            @Field("name") name: String)
            : Observable<ResponseModel>

    @FormUrlEncoded
    @POST("auth/user")
    fun postUserinfo(
            @Field("token") token: String)
            : Observable<User>

    @Multipart
    @POST("user/change-avatar")
    fun postAvatar(
            @Header("x-access-token") token:String,
            @Part file: MultipartBody.Part)
            : Observable<ResponseModel>

    @FormUrlEncoded
    @POST("user/change-password")
    fun postChangePassword(
            @Header("x-access-token") token:String,
            @Field("oldPassword") oldPassword: String,
            @Field("newPassword") newPassword: String)
            : Observable<ResponseModel>

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

    @FormUrlEncoded
    @POST("cinema/delete")
    fun postDelFilm(
            @Header("x-access-token") token:String,
            @Field("_id") _id: String)
            : Observable<ResponseModel>

    @Multipart
    @POST("cinema/edit")
    fun postEditFilm(
            @Header("x-access-token") token:String,
            @PartMap body: HashMap<String, RequestBody>,
            @Part file: MultipartBody.Part)
            : Observable<FilmModel>
}
