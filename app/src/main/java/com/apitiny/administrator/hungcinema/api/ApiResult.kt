package com.apitiny.administrator.hungcinema.api

import com.apitiny.administrator.hungcinema.model.BaseModel
import com.google.gson.JsonObject

interface ApiResult {
  
  fun onError(e: Exception)
  
  fun onModel(baseModel: BaseModel)
  
  fun onJson(jsonObject: JsonObject)
  
  fun onAPIFail()
}
