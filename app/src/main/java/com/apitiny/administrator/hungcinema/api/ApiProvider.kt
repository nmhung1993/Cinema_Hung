package com.apitiny.administrator.hungcinema.api

import android.content.Context
import android.util.Log
import com.apitiny.administrator.hungcinema.Constants
import com.apitiny.administrator.hungcinema.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiProvider {
  private val TAG = "ApiProvider"
  
  private val mApiServiceNetwork = ApiServiceNetwork.getInstance()
  
  fun callApiGetFilmList(apiResult: ApiResult) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .getFilmList()
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ListFilmResponse>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(getFilm: ListFilmResponse) {
              Log.i(TAG, "onNext")
              apiResult.onModel(getFilm)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiGetFilmDetail(apiResult: ApiResult, id: String) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .getFilmDetail(id)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<FilmDetailModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(getFilm: FilmDetailModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(getFilm)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiPostFilm(apiResult: ApiResult, parseMap: HashMap<String, RequestBody>, file: MultipartBody.Part, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postFilm(parseMap, file)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<FilmModel>() {
            override fun onCompleted() {
            
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(postFilm: FilmModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(postFilm)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiSignup(apiResult: ApiResult, name: String, email: String, password: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .signup(name, email, password)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(signUp: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(signUp)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiSignin(apiResult: ApiResult, email: String, password: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .signin(email, password)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            val errMsg: String? = null
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(signIn: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(signIn)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiResetPw(apiResult: ApiResult, email: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .resetpw(email)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(resetPw: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(resetPw)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiChangePw(apiResult: ApiResult, token: String, oldpass: String, newpass: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postChangePassword(token, oldpass, newpass)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(changPw: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(changPw)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
  }
  
  fun callApiEditName(apiResult: ApiResult, token: String, name: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postEditName(token, name)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(editName: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(editName)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiPostAvatar(apiResult: ApiResult, token: String, file: MultipartBody.Part, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postAvatar(token, file)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              //                            Toast.makeText(context, "Upload Avatar thành công!", Toast.LENGTH_SHORT).show()
              // get activity để kết thúc UploadActivity Activity
              
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(postAvatar: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(postAvatar)
            }
            
            
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
  }
  
  fun callApiUser(apiResult: ApiResult, token: String) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postUserinfo(token)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<User>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(userInfo: User) {
              Log.i(TAG, "onNext")
              apiResult.onModel(userInfo)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
  }
  
  fun callApiEditFilm(apiResult: ApiResult, token: String, parseMap: HashMap<String, RequestBody>, file: MultipartBody.Part, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postEditFilm(token, parseMap, file)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<FilmModel>() {
            override fun onCompleted() {
            
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(postEditFilm: FilmModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(postEditFilm)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
  fun callApiDelFilm(apiResult: ApiResult, token: String, id: String, context: Context) {
    try {
      mApiServiceNetwork.getNetworkService(Constants.API_ENDPOINT)
          .postDelFilm(token, id)
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Subscriber<ResponseModel>() {
            override fun onCompleted() {
              Log.e(TAG, "onCompleted")
            }
            
            override fun onError(e: Throwable) {
              Log.e(TAG, "onError" + Log.getStackTraceString(e))
              apiResult.onAPIFail()
            }
            
            override fun onNext(editFilm: ResponseModel) {
              Log.i(TAG, "onNext")
              apiResult.onModel(editFilm)
            }
          })
    } catch (e: Exception) {
      Log.e(TAG, "Exception" + Log.getStackTraceString(e))
      apiResult.onError(e)
    }
    
  }
  
}