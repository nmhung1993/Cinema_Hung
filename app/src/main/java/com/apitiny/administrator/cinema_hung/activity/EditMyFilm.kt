package com.apitiny.administrator.cinema_hung.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
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
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmModel
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

class EditMyFilm : AppCompatActivity() {

    lateinit var inputMethodManager: InputMethodManager
    lateinit var aDialog: AwesomeProgressDialog

    var prefID = PreferencesHelper(this)

    var prefValue = PreferencesHelper(this)

    var filesrc: File? = null

    var btnchonanh: Button? = null
    var btnupload: Button? = null
    var imgfilm: ImageView? = null

    private val GALLERY = 1
    private val CAMERA = 2

    lateinit var editTextDate: EditText
    lateinit var _nameED: EditText
    lateinit var _contentED: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

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
                .setTextSize(18)
                .apply()

        val _token = prefValue.getVal(application, "token")
        val _name = intent.getStringExtra("filmName")
        val _genre = intent.getStringExtra("filmGenre")
        val _releasedate = intent.getStringExtra("filmReleaseDate")
        val _content = intent.getStringExtra("filmContent")
        val _posterURL = intent.getStringExtra("filmPoster")
        val _id = intent.getStringExtra("filmID")
        val _creatorId = intent.getStringExtra("creatorId")

        _nameED = findViewById(R.id.name_ed) as EditText
        _contentED = findViewById(R.id.content_ed) as EditText
        btnchonanh = findViewById<View>(R.id.btnChonAnh) as Button
        imgfilm = findViewById<View>(R.id.imgFilm) as ImageView
        btnupload = findViewById<View>(R.id.btnUp) as Button
        editTextDate = findViewById(R.id.releaseDate_ed)

        _nameED.setText(_name)
        _contentED.setText(_content)
        editTextDate.setText(_releasedate)


        Glide.with(this@EditMyFilm)
                .load("https://cinema-hatin.herokuapp.com" + _posterURL)
                .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
                .into(imgfilm!!)

        btnchonanh!!.setOnClickListener { showPictureDialog() }
        btnupload!!.setOnClickListener {

            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

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
                btnupload!!.isEnabled = false
                aDialog.show()
                if (_creatorId == null || _creatorId == "")
                    _creatorId = ""
                if (_token != null) {
                    uploadPhim(_token, _name, _genre, milisec, _content, _creatorId, _id)
                }
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
        }
        return valid
    }

    fun uploadPhim(_token: String, name: String, genre: String, releaseDate: String, content: String, creatorId: String, id: String) {

        var imgsrc: MultipartBody.Part? = null
        var hashMap = HashMap<String, RequestBody>()

        hashMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name))
        hashMap.put("genre", RequestBody.create(MediaType.parse("text/plain"), genre))
        hashMap.put("releaseDate", RequestBody.create(MediaType.parse("text/plain"), releaseDate))
        hashMap.put("content", RequestBody.create(MediaType.parse("text/plain"), content))
        hashMap.put("creatorId", RequestBody.create(MediaType.parse("text/plain"), creatorId))
        hashMap.put("id", RequestBody.create(MediaType.parse("text/plain"), id))

        if (filesrc != null) {
            imgsrc = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))
        } else imgsrc = MultipartBody.Part.createFormData("file", "")
        //val progressDialog = AlertDialog.Builder(this)
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

        }, _token, hashMap, imgsrc, this)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Chọn ảnh:")
        val pictureDialogItems = arrayOf("Chọn từ thư viện ảnh.", "Chụp ảnh từ Camera.")
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
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
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
                    startActivityForResult(intent, CAMERA)
                }
            }
        }
    }

    fun openPickerDate(view: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            editTextDate.setText("" + dayOfMonth + "/" + month + "/" + year)
        }, year, month, day)
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
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toasty.success(this@EditMyFilm, "Chọn ảnh thành công!", Toast.LENGTH_SHORT, true).show()
                    imgfilm!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toasty.success(this@EditMyFilm, "Chọn ảnh thất bại!", Toast.LENGTH_SHORT, true).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (data!!.extras != null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imgfilm!!.setImageBitmap(thumbnail)
                saveImage(thumbnail)
                Toasty.success(this@EditMyFilm, "Chụp và lưu ảnh thành công!", Toast.LENGTH_SHORT, true).show()
            }
        }
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
}
