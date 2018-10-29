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

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {
  
  private var fileSrc: File? = null
  private val gallery = 1
  private val camera = 2
  
  private lateinit var imm: InputMethodManager
  lateinit var aDialog: AwesomeProgressDialog
  var prefValue = PreferencesHelper(this)
  
  val myListFilm: ArrayList<FilmModel> = ArrayList()
  var myFilmAdapter: MyFilmAdapter? = null
  
  //  var success: Boolean = false
  private var avatarURL: String? = ""
  private var userName: String? = ""
  private var token: String? = ""
  private var userId: String? = ""
  
  private lateinit var mHandler: Handler
  private lateinit var mRunnable: Runnable
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(activity_profile)
    
    layout_prf.setOnTouchListener { _, _ ->
      imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    
    aDialog = AwesomeProgressDialog(this)
        .setMessage("")
        .setTitle("")
//        .setDialogBodyBackgroundColor(R.color.float_transparent)
        .setColoredCircle(R.color.colorPrimary)
        .setCancelable(false)
    
    Toasty.Config.getInstance()
        .setSuccessColor(Color.parseColor("#02afee"))
        .setErrorColor(Color.parseColor("#ef5350"))
        .setTextSize(18)
        .apply()
    
    //    aDialog.show()
    
    userId = prefValue.getVal(application, "userId")
    if (userId != null) {
      getMyFilmList(userId!!)
    }
    my_film_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    myFilmAdapter = MyFilmAdapter(myListFilm, this)
    my_film_list.isNestedScrollingEnabled = false
    my_film_list.adapter = myFilmAdapter
    
    imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
    getData()
    
    avatar.setOnClickListener {
      showPictureDialog()
    }
    
    btnChangePass.setOnClickListener {
      startActivity(Intent(this@ProfileActivity, ChangePassword::class.java))
      finish()
      overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }
    
    btnSignOut.setOnClickListener { showDialog() }
    
    btnEdit.setOnClickListener {
      tv_name.setSelection(tv_name.text.length)
      tv_name.isEnabled = true
      tv_name.requestFocus()
      Companion.showSoftKeyboard(this, tv_name)
      btnEditPrf.visibility = View.VISIBLE
    }
    
    btnEditPrf.setOnClickListener {
      var result = 0
      tv_name.clearFocus()
      hideSoftKeyboard()
      aDialog.setProgressBarColor(R.color.colorPrimary).show()
      if (token != null) {
        if (fileSrc != null) {
          result += 1
        }
        if (tv_name.text.toString() == "" || tv_name.text.toString().isEmpty()) {
          tv_name.error = "Không được bỏ trống!"
          aDialog.hide()
        } else if (tv_name.text.toString() == userName) {
          tv_name.error = "Bạn cần phải chỉnh sửa tên!"
          aDialog.hide()
        } else if (tv_name.text.toString() != userName) {
          result += 2
          //          val name = tv_name.text.toString()
          //          editName(token!!, name)
          //          btnEditPrf.setVisibility(View.INVISIBLE)
          //          tv_name.setFocusable(false)
        }
        when (result) {
          1 -> {
            uploadAvatar(token!!, fileSrc!!)
            aDialog.hide()
            Toasty.success(this@ProfileActivity, "Đổi hình đại diện thành công!", Toast.LENGTH_SHORT, true).show()
          }
          2 -> {
            editName(token!!, tv_name.text.toString())
            aDialog.hide()
            btnEditPrf.visibility = View.INVISIBLE
            tv_name.isFocusable = false
            Toasty.success(this@ProfileActivity, "Đổi tên thành công!", Toast.LENGTH_SHORT, true).show()
          }
          3 -> {
            uploadAvatar(token!!, fileSrc!!)
            editName(token!!, tv_name.text.toString())
            aDialog.hide()
            btnEditPrf.visibility = View.INVISIBLE
            tv_name.isFocusable = false
            Toasty.success(this@ProfileActivity, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT, true).show()
          }
        }
        //        if (success)
        //          Toasty.success(this@ProfileActivity, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT, true).show()
      }
    }
    
    mHandler = Handler()
    swipe_refresh_prf.setOnRefreshListener {
      mRunnable = Runnable {
        getMyFilmList(userId!!)
        Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT).show()
        swipe_refresh_prf.isRefreshing = false
      }
      mHandler.postDelayed(mRunnable, 3000)
    }
    
  }
  
  private fun getData (){
    avatarURL = prefValue.getVal(application, "avatarURL")
    token = prefValue.getVal(application, "token")
    userName = prefValue.getVal(application, "userName")
  
    if (token != null || token != "")
      reloadProfile(token!!)
  
    btnEditPrf.visibility = View.INVISIBLE
    
    Glide.with(this)
        .load("https://cinema-hatin.herokuapp.com$avatarURL")
        .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
        .into(avatar)
  
    tv_name.setText(prefValue.getVal(application, "userName"))
    tv_email.text = prefValue.getVal(application, "userEmail")
  }
  
  private fun getMyFilmList(userID: String) {
    ApiProvider().callApiGetFilmList(object : ApiResult {
      override fun onError(e: Exception) {
        Toasty.error(this@ProfileActivity, "Đăng xuất thành công!", Toast.LENGTH_SHORT, true).show()
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ListFilmResponse) {
          //          aDialog.hide()
          myListFilm.clear()
          for (i in baseModel.films.indices)
            if (baseModel.films[i].user?._id == userID)
              myListFilm.add(baseModel.films[i])
          myFilmAdapter?.notifyDataSetChanged()
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
  
  
  private fun reloadProfile(token: String) {
    ApiProvider().callApiUser(object : ApiResult {
      override fun onError(e: Exception) {
        Toasty.error(this@ProfileActivity, "Không thể lấy được dữ liệu!", Toast.LENGTH_SHORT, true).show()
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is User)
          avatarURL = baseModel.avatarURL
        //                    userName = baseModel.name
        prefValue.delVal(application, "avatarURL")
        prefValue.saveVal(application, "avatarURL", avatarURL)
        //                    prefValue.delVal(application,"userName")
        //                    prefValue.saveVal(application,"userName",userName)
        Glide.with(this@ProfileActivity)
            .load("https://cinema-hatin.herokuapp.com$avatarURL")
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
  
  private fun uploadAvatar(token: String, fileSrc: File) {
    val imgSrc: MultipartBody.Part = MultipartBody.Part.createFormData("file", fileSrc.name, RequestBody.create(MediaType.parse("image/*"), fileSrc))
    
    ApiProvider().callApiPostAvatar(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          //          success = true
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
      
    }, token, imgSrc, this)
  }
  
  private fun editName(token: String, name: String) {
    
    ApiProvider().callApiEditName(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          //          success = true
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
    val aiDialog = AwesomeInfoDialog(this)
        .setTitle("ĐĂNG XUẤT")
        .setMessage("Bạn có muốn đăng xuất không?")
        .setPositiveButtonText("Đăng Xuất")
        .setColoredCircle(R.color.red_btn)
        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
        .setPositiveButtonbackgroundColor(R.color.red_btn)
        .setPositiveButtonTextColor(R.color.white)
        .setCancelable(true)
        .setPositiveButtonClick({
                                  prefValue.delVal(application, "token")
                                  prefValue.delVal(application, "userId")
                                  prefValue.delVal(application, "userEmail")
                                  prefValue.delVal(application, "userName")
                                  Toasty.success(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT, true).show()
                                  finish()
                                })
    aiDialog.show()
  }
  
  private fun showPictureDialog() {
    val aiDialog = AwesomeInfoDialog(this)
        .setTitle("Chọn nguồn ảnh:")
        .setMessage("")
        .setPositiveButtonText("Lấy từ thư viện ảnh.")
        .setNegativeButtonText("Chụp ảnh từ camera.")
        .setColoredCircle(R.color.colorPrimary)
        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
        .setPositiveButtonbackgroundColor(R.color.colorPrimaryLight)
        .setPositiveButtonTextColor(R.color.white)
        .setNegativeButtonbackgroundColor(R.color.colorPrimaryDark)
        .setNegativeButtonTextColor(R.color.white)
        .setCancelable(true)
        .setPositiveButtonClick({
                                  choosePhotoFromGallary()
                                })
        .setNegativeButtonClick({
                                  takePhotoFromCamera()
                                })
    aiDialog.show()
  }
  
  private fun choosePhotoFromGallary() {
    val permission = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.READ_EXTERNAL_STORAGE)
    
    if (permission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        100)
    } else {
      val galleryIntent = Intent(Intent.ACTION_PICK,
                                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      
      startActivityForResult(galleryIntent, gallery)
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
      startActivityForResult(intent, camera)
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
          startActivityForResult(intent, gallery)
        }
      }
      200 -> {
        
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Log.i("TAG", "Permission has been denied by user")
        } else {
          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(intent, camera)
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
    if (requestCode == gallery) {
      if (data != null) {
        val contentURI = data.data
        try {
          val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
          //                    val path = saveImage(bitmap)
          val path = getPath(contentURI)
          fileSrc = File(path)
          Toasty.success(this, "Lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
          btnEditPrf.visibility = View.VISIBLE
          avatar!!.setImageBitmap(bitmap)
          
        } catch (e: IOException) {
          e.printStackTrace()
          Toasty.error(this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT, true).show()
        }
        
      }
      
    } else if (requestCode == camera) {
      if (data != null) {
        if (data.extras != null) {
          val thumbnail = data.extras!!.get("data") as Bitmap
          avatar!!.setImageBitmap(thumbnail)
          saveImage(thumbnail)
          Toasty.success(this, "Chụp và lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
          btnEditPrf.visibility = View.VISIBLE
        }
      }
    }
  }
  
  private fun getPath(uri: Uri): String {
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = managedQuery(uri, projection, null, null, null)
    val columnIndex = cursor
        .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
    cursor.moveToFirst()
    //        val imagePath = cursor.getString(columnIndex)
    
    return cursor.getString(columnIndex)
  }
  
  private fun saveImage(myBitmap: Bitmap): String {
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
          .timeInMillis).toString() + ".jpg"))
      f.createNewFile()
      val fo = FileOutputStream(f)
      fo.write(bytes.toByteArray())
      MediaScannerConnection.scanFile(this,
                                      arrayOf(f.path),
                                      arrayOf("image/jpeg"), null)
      fo.close()
      fileSrc = f
      Log.d("TAG", "File Saved::--->" + f.absolutePath)
      
      return f.absolutePath
    } catch (e1: IOException) {
      e1.printStackTrace()
    }
    
    return ""
  }
  
  companion object {
    private const val IMAGE_DIRECTORY = "/apitiny"
    fun showSoftKeyboard(profileActivity: ProfileActivity, view: View) {
      val inputMethodManager = profileActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      view.requestFocus()
      inputMethodManager.showSoftInput(view, 0)
    }
  }
  
  private fun hideSoftKeyboard() {
    if (currentFocus != null) {
      val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    return true
  }
  
  override fun onResume() {
    super.onResume()
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    if (token != null || token != "")
      reloadProfile(token!!)
    getMyFilmList(userId!!)
  }
}