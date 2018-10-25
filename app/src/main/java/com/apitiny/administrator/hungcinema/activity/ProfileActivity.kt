package com.apitiny.administrator.hungcinema.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.R.layout.activity_profile
import com.apitiny.administrator.hungcinema.adapter.MyFilmAdapter
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.*
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
  
  var filesrc: File? = null
  private val GALLERY = 1
  private val CAMERA = 2
  
  lateinit var inputMethodManager: InputMethodManager
  lateinit var aDialog: AwesomeProgressDialog
  var prefValue = PreferencesHelper(this)
  
  val mylistFilm: ArrayList<FilmModel> = ArrayList()
  var myfilmAdapter: MyFilmAdapter? = null
  
  var success: Boolean = false
  var avatarURL: String? = ""
  var _userName: String? = ""
  var _token: String? = ""
  var userID: String? = ""
  
  lateinit var mHandler: Handler
  lateinit var mRunnable: Runnable
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(activity_profile)
    
    layout_prf.setOnTouchListener { v, event -> inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
    aDialog = AwesomeProgressDialog(this)
        .setMessage("")
        .setTitle("")
        .setDialogBodyBackgroundColor(R.color.float_transparent)
        .setColoredCircle(R.color.colorPrimary)
        .setCancelable(false)
    
    Toasty.Config.getInstance()
        .setSuccessColor(Color.parseColor("#02afee"))
        .setErrorColor(Color.parseColor("#ef5350"))
        .setTextSize(18)
        .apply()
    
    aDialog.show()
    
    userID = prefValue.getVal(application, "userID")
    if (userID != null) {
      getMyFilmList(userID!!)
    }
    my_film_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    myfilmAdapter = MyFilmAdapter(mylistFilm, this)
    my_film_list.setNestedScrollingEnabled(false)
    my_film_list.adapter = myfilmAdapter
    
    inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
    avatarURL = prefValue.getVal(application, "avatarURL")
    _token = prefValue.getVal(application, "token")
    _userName = prefValue.getVal(application, "userName")
    
    if (_token != null || _token != "")
      reloadProfile(_token!!)
    
    btnEditPrf.setVisibility(View.INVISIBLE)
    
    Glide.with(this)
        .load("https://cinema-hatin.herokuapp.com" + avatarURL)
        .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
        .into(avatar)
    
    tv_name.setText(prefValue.getVal(application, "userName"))
    tv_email.text = prefValue.getVal(application, "userEmail")
    
    avatar.setOnClickListener {
      showPictureDialog()
    }
    
    btnChangePass.setOnClickListener {
      startActivity(Intent(this@ProfileActivity, ChangePassword::class.java))
      finish()
      overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }
    
    btnSignOut!!.setOnClickListener {
      showDialog()
    }
    
    btnEdit.setOnClickListener {
      tv_name.setSelection(tv_name.getText().length)
      tv_name.setEnabled(true)
      tv_name.requestFocus()
      
      showSoftKeyboard(tv_name)
      btnEditPrf.setVisibility(View.VISIBLE)
    }
    
    btnEditPrf.setOnClickListener {
      hideSoftKeyboard()
      aDialog.setProgressBarColor(R.color.colorPrimary).show()
      if (_token != null) {
        //                when {
        //                    filesrc == null -> imgsrc = MultipartBody.Part.createFormData("file", "")
        //                    filesrc != null -> imgsrc = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))
        if (filesrc != null) {
          uploadAvatar(_token!!, filesrc!!)
          aDialog.hide()
        }
        if (tv_name.text.toString() == "" || tv_name.text.toString().isEmpty()) {
          tv_name.error = "Không được bỏ trống!"
          aDialog.hide()
        } else if (tv_name.text.toString() == _userName) {
          tv_name.error = "Bạn cần phải chỉnh sửa tên!"
          aDialog.hide()
        } else if (tv_name.text.toString() != _userName) {
          val name = tv_name.text.toString()
          editName(_token!!, name)
          btnEditPrf.setVisibility(View.INVISIBLE)
          tv_name.setFocusable(false)
        }
        if (success)
          Toasty.success(this@ProfileActivity, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT, true).show()
      }
    }
    
    mHandler = Handler()
    swipe_refresh_prf.setOnRefreshListener {
      mRunnable = Runnable {
        getMyFilmList(userID!!)
        Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT).show()
        swipe_refresh_prf.isRefreshing = false
      }
      mHandler.postDelayed(mRunnable, 3000)
    }
    
  }
  
  fun getMyFilmList(userID: String) {
    ApiProvider().callApiGetFilmList(object : ApiResult {
      override fun onError(e: Exception) {
        Toasty.error(this@ProfileActivity, "Đăng xuất thành công!", Toast.LENGTH_SHORT, true).show()
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ListFilmResponse) {
          aDialog.hide()
          mylistFilm.clear()
          for (i in baseModel.films.indices)
            if (baseModel.films[i].user?._id == userID)
              mylistFilm.add(baseModel.films[i])
          myfilmAdapter?.notifyDataSetChanged()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        Log.e("TAG", "Failed horribly")
      }
    })
  }
  
  
  fun reloadProfile(token: String) {
    ApiProvider().callApiUser(object : ApiResult {
      override fun onError(e: Exception) {
        Toasty.error(this@ProfileActivity, "Không thể lấy được dữ liệu!", Toast.LENGTH_SHORT, true).show()
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is User)
          avatarURL = baseModel.avatarURL
        //                    _userName = baseModel.name
        prefValue.delVal(application, "avatarURL")
        prefValue.saveVal(application, "avatarURL", avatarURL)
        //                    prefValue.delVal(application,"userName")
        //                    prefValue.saveVal(application,"userName",_userName)
        Glide.with(this@ProfileActivity)
            .load("https://cinema-hatin.herokuapp.com" + avatarURL)
            .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
            .into(avatar)
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        Log.e("TAG", "Failed horribly")
      }
    }, token)
  }
  
  fun uploadAvatar(token: String, filesrc: File) {
    var imgsrc: MultipartBody.Part = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))
    
    ApiProvider().callApiPostAvatar(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          success = true
          avatarURL = baseModel.isResponse?.avatarURL
          prefValue.delVal(application, "avatarURL")
          prefValue.saveVal(application, "avatarURL", avatarURL)
          aDialog.hide()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        Log.e("TAG", "Failed horribly")
      }
      
    }, token, imgsrc, this)
  }
  
  fun editName(token: String, name: String) {
    
    ApiProvider().callApiEditName(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          success = true
          prefValue.delVal(application, "username")
          prefValue.saveVal(application, "userName", baseModel.isResponse?.name)
          aDialog.hide()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        Log.e("TAG", "Failed horribly")
      }
      
    }, token, name, this)
  }
  
  //    @SuppressLint("ResourceAsColor")
  private fun showDialog() {
    var aiDialog = AwesomeInfoDialog(this)
        .setTitle("ĐĂNG XUẤT")
        .setMessage("Bạn có muốn đăng xuất không?")
        .setPositiveButtonText("Đăng Xuất")
        .setColoredCircle(R.color.red_btn)
        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
        .setPositiveButtonbackgroundColor(R.color.red_btn)
        .setPositiveButtonTextColor(R.color.white)
        .setCancelable(true)
        .setPositiveButtonClick(Closure() {
          prefValue.delVal(application, "token")
          prefValue.delVal(application, "userID")
          prefValue.delVal(application, "userEmail")
          prefValue.delVal(application, "userName")
          Toasty.success(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT, true).show()
          finish()
        })
    aiDialog.show()
  }
  
  private fun showPictureDialog() {
    val pictureDialog = android.app.AlertDialog.Builder(this)
    pictureDialog.setTitle("Đổi Avatar")
    val pictureDialogItems = arrayOf("Chọn ảnh từ thư viện ảnh", "Chụp ảnh từ camera")
    pictureDialog.setItems(pictureDialogItems
    ) { dialog, which ->
      when (which) {
        0 -> choosePhotoFromGallary()
        1 -> takePhotoFromCamera()
      }
    }
    pictureDialog.show()
  }
  
  fun choosePhotoFromGallary() {
    val permission = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.READ_EXTERNAL_STORAGE)
    
    if (permission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        100)
    } else {
      val galleryIntent = Intent(Intent.ACTION_PICK,
                                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      
      startActivityForResult(galleryIntent, GALLERY)
    }
  }
  
  private fun takePhotoFromCamera() {
    val permission = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.CAMERA)
    
    if (permission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
                                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        200)
    } else {
      val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      startActivityForResult(intent, CAMERA)
    }
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    when (requestCode) {
      100 -> {
        
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Log.i("TAG", "Permission has been denied by user")
        } else {
          val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          startActivityForResult(intent, GALLERY)
        }
      }
      200 -> {
        
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Log.i("TAG", "Permission has been denied by user")
        } else {
          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(intent, CAMERA)
        }
      }
    }
  }
  
  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    
    super.onActivityResult(requestCode, resultCode, data)
    /* if (resultCode == this.RESULT_CANCELED)
     {
     return
     }*/
    if (requestCode == GALLERY) {
      if (data != null) {
        val contentURI = data.data
        try {
          val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
          //                    val path = saveImage(bitmap)
          val path = getPath(contentURI)
          filesrc = File(path)
          Toasty.success(this, "Lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
          btnEditPrf.setVisibility(View.VISIBLE)
          avatar!!.setImageBitmap(bitmap)
          
        } catch (e: IOException) {
          e.printStackTrace()
          Toasty.error(this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT, true).show()
        }
        
      }
      
    } else if (requestCode == CAMERA) {
      if (data != null) {
        if (data.extras != null) {
          val thumbnail = data!!.extras!!.get("data") as Bitmap
          avatar!!.setImageBitmap(thumbnail)
          saveImage(thumbnail)
          Toasty.success(this, "Chụp và lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
          btnEditPrf.setVisibility(View.VISIBLE)
        }
      }
    }
  }
  
  fun getPath(uri: Uri): String {
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = managedQuery(uri, projection, null, null, null)
    val column_index = cursor
        .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
    cursor.moveToFirst()
    //        val imagePath = cursor.getString(column_index)
    
    return cursor.getString(column_index)
  }
  
  fun saveImage(myBitmap: Bitmap): String {
    val bytes = ByteArrayOutputStream()
    myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
    val wallpaperDirectory = File(
        (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
    // have the object build the directory structure, if needed.
    Log.d("fee", wallpaperDirectory.toString())
    if (!wallpaperDirectory.exists()) {
      wallpaperDirectory.mkdirs()
    }
    
    try {
      Log.d("heel", wallpaperDirectory.toString())
      val f = File(wallpaperDirectory, ((Calendar.getInstance()
          .getTimeInMillis()).toString() + ".jpg"))
      f.createNewFile()
      val fo = FileOutputStream(f)
      fo.write(bytes.toByteArray())
      MediaScannerConnection.scanFile(this,
                                      arrayOf(f.getPath()),
                                      arrayOf("image/jpeg"), null)
      fo.close()
      filesrc = f
      Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())
      
      return f.getAbsolutePath()
    } catch (e1: IOException) {
      e1.printStackTrace()
    }
    
    return ""
  }
  
  companion object {
    private val IMAGE_DIRECTORY = "/apitiny"
  }
  
  fun hideSoftKeyboard() {
    if (currentFocus != null) {
      val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
  }
  
  /**
   * Shows the soft keyboard
   */
  fun showSoftKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, 0)
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    return true
  }
  
  override fun onResume() {
    super.onResume()
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    if (_token != null || _token != "")
      reloadProfile(_token!!)
    getMyFilmList(userID!!)
  }
}