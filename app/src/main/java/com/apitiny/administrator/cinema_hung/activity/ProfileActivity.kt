package com.apitiny.administrator.cinema_hung.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    var filesrc: File? = null
    private val GALLERY = 1
    private val CAMERA = 2

    var prefValue = PreferencesHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnEditPrf.setVisibility(View.INVISIBLE)

        tv_name.text = prefValue.getVal(application,"userName")
        tv_email.text = prefValue.getVal(application,"userEmail")

        avatar.setOnClickListener{
            showPictureDialog()
        }

        btnSignOut!!.setOnClickListener{
            showDialog()
//                prefValue.delVal(application,"token")
        }

    }

    private fun showDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog:AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("ĐĂNG XUẤT")

        // Set a message for alert dialog
        builder.setMessage("Bạn có muốn đăng xuất không?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    prefValue.delVal(application,"token")
                    prefValue.delVal(application,"userID")
                    prefValue.delVal(application,"userEmail")
                    prefValue.delVal(application,"userName")
                    toast("Đăng Xuất thành công.")
                    finish()
                }
//                DialogInterface.BUTTON_NEGATIVE -> toast("Negative/No button clicked.")
//                DialogInterface.BUTTON_NEUTRAL -> toast("Hủy đăng nhập.")
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Đăng Xuất",dialogClickListener)

        // Set the alert dialog negative/no button
//        builder.setNegativeButton("NO",dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("Quay Lại",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
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
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                avatar!!.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }

        }

    } else if (requestCode == CAMERA) {
        if (data!!.extras != null) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            avatar!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
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
        btnProfile.setVisibility(View.VISIBLE)
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

// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

}