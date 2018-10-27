package com.apitiny.administrator.hungcinema.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.BaseModel
import com.apitiny.administrator.hungcinema.model.FilmModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
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

class UploadActivity : AppCompatActivity() {
  
  lateinit var inputMethodManager: InputMethodManager
  
  lateinit var aDialog: AwesomeProgressDialog
  
  var prefID = PreferencesHelper(this)
  var _nameED: EditText? = null
  var _contentED: EditText? = null
  
  var filesrc: File? = null
  
  var btnchonanh: Button? = null
  var btnupload: Button? = null
  var imgfilm: ImageView? = null
  
  private val GALLERY = 1
  private val CAMERA = 2
  
  lateinit var editTextDate: EditText
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_upload)
    
    layout_upload.setOnTouchListener { v, event -> inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
    inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
    aDialog = AwesomeProgressDialog(this)
        .setMessage("")
        .setTitle("")
        .setDialogBodyBackgroundColor(R.color.float_transparent)
        .setColoredCircle(R.color.colorPrimary)
        .setCancelable(false)
    
    Toasty.Config.getInstance()
        .setSuccessColor(Color.parseColor("#02afee"))
        .setErrorColor(Color.parseColor("#ef5350"))
        .setWarningColor(Color.parseColor("#ef5350"))
        .setTextSize(18)
        .apply()
    
    val date_n = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    
    _nameED = findViewById(R.id.name_ed) as EditText
    _contentED = findViewById(R.id.content_ed) as EditText
    btnchonanh = findViewById<View>(R.id.btnChonAnh) as Button
    imgfilm = findViewById<View>(R.id.imgFilm) as ImageView
    btnupload = findViewById<View>(R.id.btnUp) as Button
    editTextDate = findViewById(R.id.releaseDate_ed)
    editTextDate.setText(date_n)
    
    editTextDate.setOnClickListener {
      //      editTextDate.isEnabled = false
      openPickerDate()
    }
    
    btnchonanh!!.setOnClickListener { showPictureDialog() }
    btnupload!!.setOnClickListener {
      hideSoftKeyboard()
      
      val _name = name_ed.text.toString()
      val _genre = genre_sp.getSelectedItem().toString()
      val _releaseDate = releaseDate_ed.text.toString()
      val _content = content_ed.text.toString()
      var _creatorId = prefID.getVal(application, "userID")
      
      val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
      var milisec: String = ""
      //chuyển đổi ngày sang mili giây
      try {
        val d: Date = df.parse(_releaseDate)
        val l: Long = d.time
        milisec = l.toString()
      } catch (e: ParseException) {
        e.printStackTrace()
      }
      
      if (validate(_name, _content, filesrc)) {
        aDialog.show()
        if (_creatorId == null || _creatorId == "")
          _creatorId = ""
        uploadPhim(_name, _genre, milisec, _content, _creatorId)
      }
      
    }
    
  }
  
  fun validate(name: String, content: String, file: File?): Boolean {
    var valid = true
    
    if (name!!.isEmpty()) {
      name_ed!!.error = "Tên phim không được bỏ trống!"
      valid = false
    } else if (content.isEmpty()) {
      content_ed!!.error = "Vui lòng nhập mô tả phim"
      valid = false
    } else if (file == null) {
      Toasty.warning(this, "Bạn chưa chọn hình!", Toast.LENGTH_SHORT, true).show()
      valid = false
    }
    return valid
  }
  
  fun uploadPhim(name: String, genre: String, releaseDate: String, content: String, creatorId: String?) {
    
    var hashMap = HashMap<String, RequestBody>()
    
    hashMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name))
    hashMap.put("genre", RequestBody.create(MediaType.parse("text/plain"), genre))
    hashMap.put("releaseDate", RequestBody.create(MediaType.parse("text/plain"), releaseDate))
    hashMap.put("content", RequestBody.create(MediaType.parse("text/plain"), content))
    hashMap.put("creatorId", RequestBody.create(MediaType.parse("text/plain"), creatorId))
    
    var imgsrc: MultipartBody.Part = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))
    ApiProvider().callApiPostFilm(object : ApiResult {
      override fun onError(e: Exception) {
        toast("Error")
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is FilmModel) {
          Toasty.success(this@UploadActivity, "Tạo phim thành công!", Toast.LENGTH_SHORT, true).show()
          startActivity(Intent(this@UploadActivity, MainActivity::class.java))
          finish()
          overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        toast("Json")
        
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        toast("APIFail")
        Log.e("TAG", "Failed horribly")
      }
      
    }, hashMap, imgsrc, this)
  }
  
  private fun showPictureDialog() {
    var aiDialog = AwesomeInfoDialog(this)
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
        .setPositiveButtonClick(Closure() {
          choosePhotoFromGallary()
        })
        .setNegativeButtonClick(Closure() {
          takePhotoFromCamera()
        })
    aiDialog.show()
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
  
  fun openPickerDate() {
    val c = Calendar.getInstance()
    val _year = c.get(Calendar.YEAR)
    val _month = c.get(Calendar.MONTH)
    val _day = c.get(Calendar.DAY_OF_MONTH)
    
    val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
      editTextDate.setText("" + dayOfMonth + "/" + month + "/" + year)
    }, _year, _month, _day)
    dpd.show()
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
          val path = getPath(contentURI)
          filesrc = File(path)
          Toasty.success(this, "", Toast.LENGTH_SHORT, true).show()
          imgfilm!!.setImageBitmap(bitmap)
          
        } catch (e: IOException) {
          e.printStackTrace()
          Toasty.error(this, "", Toast.LENGTH_SHORT, true).show()
        }
        
      }
      
    } else if (requestCode == CAMERA) {
      if (data != null) {
        if (data.extras != null) {
          val thumbnail = data!!.extras!!.get("data") as Bitmap
          imgfilm!!.setImageBitmap(thumbnail)
          saveImage(thumbnail)
          Toasty.success(this, "", Toast.LENGTH_SHORT, true).show()
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
  
  fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
  
  fun hideSoftKeyboard() {
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
  }
  
  companion object {
    private val IMAGE_DIRECTORY = "/apitiny"
  }
}