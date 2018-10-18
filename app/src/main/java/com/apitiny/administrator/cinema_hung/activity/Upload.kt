package com.apitiny.administrator.cinema_hung.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
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

class Upload : AppCompatActivity() {


    var tv : TextView ?= null
    var filesrc : File? = null
    private var btn: Button? = null
    private var btnup: Button? = null
    private var imageview: ImageView? = null
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

        btn = findViewById<View>(R.id.btnChonAnh) as Button
        imageview = findViewById<View>(R.id.imgFilm) as ImageView
        btn!!.setOnClickListener { showPictureDialog() }

        editTextDate = findViewById(R.id.releaseDate_ed)

        btnup = findViewById<View>(R.id.btnUp) as Button
        btnup!!.setOnClickListener {
            val name : String = name_ed.text.toString()
            val genre : String = genre_sp.getSelectedItem().toString()
            val releaseDate : String = releaseDate_ed.text.toString()
            val content : String = content_ed.text.toString()

            val df : DateFormat = SimpleDateFormat("dd/MM/yyyy")
            var milisec : String = ""
            //chuyển đổi ngày sang mili giây
            try {
                val d : Date = df.parse(releaseDate)
                val l : Long = d.time
                milisec = l.toString()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (name!="" && content!="")
                uploadPhim(name,genre,milisec,content)
            else{
                Toast.makeText(this, "Bạn phải nhập đầy đủ thông tin phim!", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun uploadPhim(name:String,genre:String,releaseDate:String,content:String){

        var hashMap = HashMap<String, RequestBody>()

        hashMap.put("name", RequestBody.create(MediaType.parse("text/plain"),name))
        hashMap.put("genre", RequestBody.create(MediaType.parse("text/plain"),genre))
        hashMap.put("releaseDate", RequestBody.create(MediaType.parse("text/plain"),releaseDate))
        hashMap.put("content", RequestBody.create(MediaType.parse("text/plain"),content))

        var imgsrc :MultipartBody.Part = MultipartBody.Part.createFormData("file", filesrc?.name,RequestBody.create(MediaType.parse("image/*"),filesrc) )
        //val progressDialog = AlertDialog.Builder(this)
        ApiProvider().callApiPost(object: ApiResult{
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is FilmModel) {
//                    imgsrc = MultipartBody.Part.createFormData("file", filesrc?.name,RequestBody.create(MediaType.parse("image/*"),filesrc) )
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Log.e("TAG", "Failed horribly")
            }

        },hashMap,imgsrc,this)
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
        }else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }
    }

    private  fun makeRequest(){
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
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

    fun openPickerDate(view:View){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> editTextDate.setText("" + dayOfMonth + "/" + month + "/" + year)
        }, year,month,day)
        dpd.show()
    }


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@Upload, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageview!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@Upload, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            if (data!!.extras != null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imageview!!.setImageBitmap(thumbnail)
                saveImage(thumbnail)
                Toast.makeText(this@Upload, "Image Saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
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
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/apitiny"
    }
}