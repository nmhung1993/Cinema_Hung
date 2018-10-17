package com.apitiny.administrator.cinema_hung.api

import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.google.gson.JsonObject

interface ApiResult {

    fun onError(e: Exception)

    fun onModel(baseModel: BaseModel)

    fun onJson(jsonObject: JsonObject)

    fun onAPIFail()
}
