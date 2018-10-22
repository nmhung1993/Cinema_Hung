package com.apitiny.administrator.cinema_hung.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmDetailModel
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_filmdetail.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FilmDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filmdetail)

        val _id = intent.getStringExtra("_id")

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

                    Glide.with(this@FilmDetailActivity)
                            .load("https://cinema-hatin.herokuapp.com" + baseModel.cinema.posterURL)
                            .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
                            .into(imgFilm)

                    name.text = baseModel.cinema.name
                    genre.text = baseModel.cinema.genre
                    releaseDate.text = dateString
                    creatorName.text = baseModel.cinema.user?.name
                    content.text = baseModel.cinema.content

                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Toast.makeText(baseContext, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_LONG).show()
                Log.e("TAG", "Failed horribly")
            }

        }, _id)

    }


}