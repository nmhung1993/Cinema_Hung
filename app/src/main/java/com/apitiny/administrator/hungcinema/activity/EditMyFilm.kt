package com.apitiny.administrator.hungcinema.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.BaseModel
import com.apitiny.administrator.hungcinema.model.FilmModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_upload.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditMyFilm : AppCompatActivity() {
  
  private lateinit var imm: InputMethodManager
  private lateinit var aDialog: AwesomeProgressDialog
  
  private var prefID = PreferencesHelper(this)
  private var prefValue = PreferencesHelper(this)
  private var fileSrc: File? = null
  
  var btnChonAnh: Button? = null
  var btnUp: Button? = null
  var imgFilm: ImageView? = null
  
  private val gallery = 1
  private val camera = 2
  
  @SuppressLint("SimpleDateFormat")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_upload)
    
    imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
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
    
    val token = prefValue.getVal(application, "token")
    var creatorId = prefID.getVal(application, "userId")
    var name = intent.getStringExtra("filmName")
    var genre: String
    var releaseDate = intent.getStringExtra("filmReleaseDate")
    var content = intent.getStringExtra("filmContent")
    val posterURL = intent.getStringExtra("filmPoster")
    val id = intent.getStringExtra("filmID")
    
    
    name_ed.setText(name)
    content_ed.setText(content)
    releaseDate_ed.setText(releaseDate)
    
    imgFilm?.let {
      Glide.with(this@EditMyFilm)
          .load("https://cinema-hatin.herokuapp.com$posterURL")
          .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
          .into(it)
    }
    
    btnChonAnh?.setOnClickListener { showPictureDialog() }
    btnUp?.setOnClickListener {
      
      imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
      
      name = name_ed.text.toString()
      genre = genre_sp.selectedItem.toString()
      releaseDate = releaseDate_ed.text.toString()
      content = content_ed.text.toString()
      
      
      val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
      var milisec = ""
      //chuyển đổi ngày sang mili giây
      try {
        val d: Date = df.parse(releaseDate)
        val l: Long = d.time
        milisec = l.toString()
      } catch (e: ParseException) {
        e.printStackTrace()
      }
      
      if (validate(name, content)) {
        btnUp!!.isEnabled = false
        aDialog.show()
        if (creatorId == null)
          creatorId = ""
        if (token != null) uploadPhim(token, name, genre, milisec, content, creatorId!!, id)
      }
      
    }
    
  }
  
  private fun validate(name: String, content: String): Boolean {
    var valid = true
    
    if (name.isEmpty()) {
      name_ed.error = "Tên phim không được bỏ trống!"
      valid = false
    } else if (content.isEmpty()) {
      content_ed.error = "Vui lòng nhập mô tả phim"
      valid = false
    }
    return valid
  }
  
  private fun uploadPhim(_token: String, name: String, genre: String, releaseDate: String, content: String, creatorId: String, id: String) {
    
    val imgSrc: MultipartBody.Part? = if (fileSrc != null) {
      MultipartBody.Part.createFormData("file", fileSrc?.name, RequestBody.create(MediaType.parse("image/*"), fileSrc))
    } else MultipartBody.Part.createFormData("file", "")
    val hashMap = HashMap<String, RequestBody>()
    
    hashMap["name"] = RequestBody.create(MediaType.parse("text/plain"), name)
    hashMap["genre"] = RequestBody.create(MediaType.parse("text/plain"), genre)
    hashMap["releaseDate"] = RequestBody.create(MediaType.parse("text/plain"), releaseDate)
    hashMap["content"] = RequestBody.create(MediaType.parse("text/plain"), content)
    hashMap["creatorId"] = RequestBody.create(MediaType.parse("text/plain"), creatorId)
    hashMap["id"] = RequestBody.create(MediaType.parse("text/plain"), id)
    
    //val progressDialog = AlertDialog.Builder(this)
    if (imgSrc != null) {
      ApiProvider().callApiEditFilm(object : ApiResult {
        override fun onError(e: Exception) {
          Log.e("TAG", e.message)
        }
        
        override fun onModel(baseModel: BaseModel) {
          if (baseModel is FilmModel) {
            Toasty.success(this@EditMyFilm, "Sửa phim thành công!", Toast.LENGTH_SHORT, true).show()
            startActivity(Intent(this@EditMyFilm, MainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
          }
        }
        
        override fun onJson(jsonObject: JsonObject) {
          Log.e("TAG", "Received a different model")
        }
        
        override fun onAPIFail() {
          Log.e("TAG", "Failed horribly")
        }
        
      }, _token, hashMap, imgSrc, this)
    }
  }
  
  private fun showPictureDialog() {
    val pictureDialog = AlertDialog.Builder(this)
    pictureDialog.setTitle("Chọn ảnh:")
    val pictureDialogItems = arrayOf("Chọn từ thư viện ảnh.", "Chụp ảnh từ Camera.")
    pictureDialog.setItems(pictureDialogItems
    ) { _, which ->
      when (which) {
        0 -> choosePhotoFromGallary()
        1 -> takePhotoFromCamera()
      }
    }
    pictureDialog.show()
  }
  
  private fun choosePhotoFromGallary() {
    val galleryIntent = Intent(Intent.ACTION_PICK,
                               MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    
    startActivityForResult(galleryIntent, gallery)
  }
  
  private fun takePhotoFromCamera() {
    val permission = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.CAMERA)
    
    if (permission != PackageManager.PERMISSION_GRANTED) {
      makeRequest()
    } else {
      val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      startActivityForResult(intent, camera)
    }
  }
  
  private fun makeRequest() {
    ActivityCompat.requestPermissions(this,
                                      arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
  }
  
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    when (requestCode) {
      100 -> {
        
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Log.i("TAG", "Permission has been denied by user")
        } else {
          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(intent, camera)
        }
      }
    }
  }
  
  //  fun openPickerDate(view: View) {
  //    val c = Calendar.getInstance()
  //    val year = c.get(Calendar.YEAR)
  //    val month = c.get(Calendar.MONTH) + 1
  //    val day = c.get(Calendar.DAY_OF_MONTH)
  //
  //    val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
  //      releaseDate_ed.setText("" + dayOfMonth + "/" + month + "/" + year)
  //    }, year, month, day)
  //    dpd.show()
  //  }
  
  
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
          saveImage(bitmap)
          Toasty.success(this@EditMyFilm, "Chọn ảnh thành công!", Toast.LENGTH_SHORT, true).show()
          imgFilm!!.setImageBitmap(bitmap)
          
        } catch (e: IOException) {
          e.printStackTrace()
          Toasty.success(this@EditMyFilm, "Chọn ảnh thất bại!", Toast.LENGTH_SHORT, true).show()
        }
        
      }
      
    } else if (requestCode == camera) {
      if (data != null) {
        if (data.extras != null) {
          val thumbnail = data.extras!!.get("data") as Bitmap
          imgFilm!!.setImageBitmap(thumbnail)
          saveImage(thumbnail)
          Toasty.success(this@EditMyFilm, "Chụp và lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
        }
      }
    }
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
  }
}
