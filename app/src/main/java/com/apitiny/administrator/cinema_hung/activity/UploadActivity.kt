package com.apitiny.administrator.cinema_hung.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.google.gson.JsonObject
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

//        tv = findViewById(R.id.tv_test) as TextView
//        var font : Typeface = Typeface.createFromAsset(getAssets(),"font/fontawesome_awesome.ttf")
//        tv!!.setTypeface(font)
////        tv!!.text = "\f03d"

        _nameED = findViewById(R.id.name_ed) as EditText
        _contentED = findViewById(R.id.content_ed) as EditText
        btnchonanh = findViewById<View>(R.id.btnChonAnh) as Button
        imgfilm = findViewById<View>(R.id.imgFilm) as ImageView
        btnupload = findViewById<View>(R.id.btnUp) as Button

        editTextDate = findViewById(R.id.releaseDate_ed)

        val date_n = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        editTextDate.setText(date_n)




        btnchonanh!!.setOnClickListener { showPictureDialog() }
        btnupload!!.setOnClickListener {

//            var _imgsrc: MultipartBody.Part = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))

            val _name = name_ed.text.toString()
            val _genre = genre_sp.getSelectedItem().toString()
            val _releaseDate = releaseDate_ed.text.toString()
            val _content = content_ed.text.toString()
            var _creatorId = prefID.getVal(application,"userID")

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

            if (validate(_name,_content,filesrc))
                if (_creatorId==null || _creatorId=="")
                    _creatorId = ""
                uploadPhim(_name, _genre, milisec, _content, _creatorId)
        }

    }

    fun validate(name:String, content:String, file:File?): Boolean {
        var valid = true

        if (name!!.isEmpty()) {
            name_ed!!.error = "Tên phim không được bỏ trống!"
            valid = false
        } else if (content.isEmpty()) {
            content_ed!!.error = "Vui lòng nhập mô tả phim"
            valid = false
        } else if (file == null){
            Toast.makeText(baseContext, "Bạn chưa chọn hình!", Toast.LENGTH_LONG).show()
            valid = false
        }
        return valid
    }

    fun uploadPhim(name:String, genre:String, releaseDate:String, content:String, creatorId:String?) {

        var hashMap = HashMap<String, RequestBody>()

        hashMap.put("name", RequestBody.create(MediaType.parse("text/plain"), name))
        hashMap.put("genre", RequestBody.create(MediaType.parse("text/plain"), genre))
        hashMap.put("releaseDate", RequestBody.create(MediaType.parse("text/plain"), releaseDate))
        hashMap.put("content", RequestBody.create(MediaType.parse("text/plain"), content))
        hashMap.put("creatorId", RequestBody.create(MediaType.parse("text/plain"), creatorId))

        var imgsrc: MultipartBody.Part = MultipartBody.Part.createFormData("file", filesrc?.name, RequestBody.create(MediaType.parse("image/*"), filesrc))
        //val progressDialog = AlertDialog.Builder(this)
        ApiProvider().callApiPostFilm(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is FilmModel) {
//                    imgsrc = MultipartBody.Part.createFormData("file", filesrc?._name,RequestBody.create(MediaType.parse("image/*"),filesrc) )
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Log.e("TAG", "Failed horribly")
            }

        }, hashMap, imgsrc, this)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
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
        val month = c.get(Calendar.MONTH)+1
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
                    Toast.makeText(this@UploadActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imgfilm!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@UploadActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (data!!.extras != null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imgfilm!!.setImageBitmap(thumbnail)
                saveImage(thumbnail)
                Toast.makeText(this@UploadActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
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