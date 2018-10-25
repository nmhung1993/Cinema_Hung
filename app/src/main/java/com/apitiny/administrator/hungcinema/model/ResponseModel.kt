package com.apitiny.administrator.hungcinema.model

import com.google.gson.annotations.SerializedName

class ResponseModel : BaseModel {
  
  @SerializedName("status")
  var isStatus: Int = 0
  @SerializedName("token")
  var isToken: String? = null
  @SerializedName("user")
  var isUser: User? = null
  @SerializedName("errorMessage")
  var isErrormsg: String? = null
  @SerializedName("error")
  var isError: String? = null
  @SerializedName("message")
  var isMessage: String? = null
  @SerializedName("response")
  var isResponse: User? = null
  
}