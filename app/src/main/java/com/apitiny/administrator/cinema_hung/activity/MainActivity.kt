package com.apitiny.administrator.cinema_hung.activity

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.adapter.FilmAdapter
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var prefValue = PreferencesHelper(this)
    private val TAG = "ApiProvider"
    val listFilm: ArrayList<FilmModel> = ArrayList()
    var listFilmSearch: ArrayList<FilmModel> = ArrayList()
    var filmAdapter: FilmAdapter? = null

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getListFilm()
        rv_film_list.layoutManager = LinearLayoutManager(this)
        filmAdapter = FilmAdapter(listFilm, this)
        rv_film_list.adapter = filmAdapter

        if (prefValue.getVal(application, "token") == null)
            btnProfile.setVisibility(View.INVISIBLE)
        else btnProfile.setVisibility(View.VISIBLE)

        btnProfile.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnUpload.setOnClickListener {
            if (prefValue.getVal(application, "token") == null) showDialog()
            else {
                val intent = Intent(applicationContext, UploadActivity::class.java)
                startActivity(intent)
            }

        }

        mHandler = Handler()
        swipe_refresh_layout.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                getListFilm()
                Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT).show()
                // Hide swipe to refresh icon animation
                swipe_refresh_layout.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 3000)
        }

    }

    //get List Phim
    fun getListFilm() {
        ApiProvider().callApiGetFilmList(object : ApiResult {
            override fun onError(e: Exception) {
                Toast.makeText(applicationContext, "Không thể lấy được phim", Toast.LENGTH_SHORT).show()
                Log.e(TAG, e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ListFilmResponse) {
                    //xóa hết dữ liêu cũ và add dữ liệu mới
                    listFilm.clear()
                    listFilm.addAll(baseModel.films)

                    filmAdapter?.notifyDataSetChanged()
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e(TAG, "Received a different model")
            }

            override fun onAPIFail() {
                Log.e(TAG, "Failed horribly")
            }
        })
    }

    fun filter(charText: String?) {
        var charText = charText
        listFilmSearch.clear()
        if (charText == null || charText == "") {
            listFilmSearch.addAll(listFilm)
        } else {
            charText = charText!!.toLowerCase(Locale.getDefault())
            for (wp in listFilm) {
                if (wp.name != null) {
                    if (wp.name?.toLowerCase()!!.contains(charText.toLowerCase()) || wp.name!!.contains(charText)) {
                        listFilmSearch.add(wp)
                    }
                }
            }
        }
        filmAdapter?.setSearchResult(listFilmSearch)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.btnSearch)

        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView

            val searchHint = getString(R.string.searchHint)
            searchView.setQueryHint(searchHint)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
//                    if (query!!.toString().isNotEmpty()) {
////                        startRecyclerView(generateData(newText))
////                        listFilm.add()
//                        filter(query)
//                        listFilm.clear()
//                    }
//                    else {
//
//                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    filter(newText)
                    return false
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun showDialog() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("ĐĂNG NHẬP")

        // Set a message for alert dialog
        builder.setMessage("Bạn có muốn đăng nhập không?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
//                    prefValue.delVal(application,"token")
                    val intent = Intent(applicationContext, SigninActivity::class.java)
                    startActivity(intent)
                }
//                DialogInterface.BUTTON_NEGATIVE -> toast("Negative/No button clicked.")
//                DialogInterface.BUTTON_NEUTRAL -> {
//                    val intent = Intent(applicationContext, UploadActivity::class.java)
//                    startActivity(intent)
//                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Đăng Nhập", dialogClickListener)

        // Set the alert dialog negative/no button
//        builder.setNegativeButton("NO",dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("Trở lại", dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    //khi quay lại sẽ get lại list film
    override fun onResume() {
        super.onResume()
        getListFilm()
        if (prefValue.getVal(application, "token") == null)
            btnProfile.setVisibility(View.INVISIBLE)
        else btnProfile.setVisibility(View.VISIBLE)
    }
}
