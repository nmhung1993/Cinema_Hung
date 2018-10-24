package com.apitiny.administrator.cinema_hung.model

import com.google.gson.annotations.SerializedName

class User : BaseModel{
    @SerializedName("_id")
    var _id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("avatarURL")
    var avatarURL:String? = null
}