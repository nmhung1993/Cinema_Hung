package com.apitiny.administrator.cinema_hung.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmDetailModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_filmdetail.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FilmDetailActivity : AppCompatActivity() {

    lateinit var inputMethodManager: InputMethodManager
    lateinit var aDialog: AwesomeProgressDialog

    var prefValue = PreferencesHelper(this)
    var _userID: String? = null
    var _token: String? = null
    var _posterURL: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filmdetail)

        btnEditFilm.setVisibility(View.INVISIBLE)
        btnDelFilm.setVisibility(View.INVISIBLE)

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

        _userID = prefValue.getVal(application, "userID")
        _token = prefValue.getVal(application, "token")

        val _id = intent.getStringExtra("_id")

        aDialog.show()

        ApiProvider().callApiGetFilmDetail(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is FilmDetailModel) {

                    val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                    var milisec: String = baseModel.cinema.releaseDate.toString()
                    if (milisec == null || milisec == "null") {
                        milisec = "1"
                    }

                    var dateString: String = ""
                    //chuyển đổi ngày sang mili giây
                    try {
                        val l = milisec.toLong()
                        val d: Date = Date(l)
                        dateString = df.format(d)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    _posterURL = baseModel.cinema.posterURL

                    name.text = baseModel.cinema.name
                    genre.text = baseModel.cinema.genre
                    releaseDate.text = dateString
                    creatorName.text = baseModel.cinema.user?.name
                    content.text = baseModel.cinema.content

                    Glide.with(this@FilmDetailActivity)
                            .load("https://cinema-hatin.herokuapp.com" + _posterURL)
                            .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
                            .into(imgFilm)

                    if (baseModel.cinema.user?._id != null)
                        if (validateID(baseModel.cinema.user?._id!!)) {
                            btnEditFilm.setVisibility(View.VISIBLE)
                            btnDelFilm.setVisibility(View.VISIBLE)
                        }

                    aDialog.hide()
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Log.e("TAG", "Failed horribly")
            }

        }, _id)

        btnDelFilm.setOnClickListener {
            if (_token != null) {
                var aiDialog = AwesomeInfoDialog(this)
                        .setTitle("XÓA PHIM")
                        .setMessage("Bạn có chắc chắn muốn xóa không?")
                        .setNeutralButtonText("XÓA")
                        .setColoredCircle(R.color.red_btn)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setPositiveButtonbackgroundColor(R.color.red_btn)
                        .setPositiveButtonTextColor(R.color.white)
                        .setCancelable(true)
                        .setPositiveButtonClick(Closure() {
                            aDialog.show()
                            delFilm(_token!!, _id)
                        })
                aiDialog.show()
            }
        }

        btnEditFilm.setOnClickListener {
            if (_token != null) {
                val intent = Intent(this@FilmDetailActivity, EditMyFilm::class.java)
                intent.putExtra("filmName", name.text.toString())
                intent.putExtra("filmGenre", genre.text.toString())
                intent.putExtra("filmReleaseDate", releaseDate.text.toString())
                intent.putExtra("filmContent", content.text.toString())
                intent.putExtra("filmPoster", _posterURL)
                intent.putExtra("creatorId", _userID)
                intent.putExtra("filmID", _id)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
            }
        }

    }

    fun delFilm(token: String, id: String) {
        ApiProvider().callApiDelFilm(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ResponseModel) {
                    Toasty.success(this@FilmDetailActivity, "Xóa phim thành công!", Toast.LENGTH_SHORT, true).show()
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

        }, token, id, this)
    }

    fun validateID(uid: String): Boolean {
        var valid = false

        if (_userID == uid) {
            valid = true
        }
        return valid
    }
}