package com.apitiny.administrator.cinema_hung.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.adapter.FilmAdapter
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.FilmModel
import com.apitiny.administrator.cinema_hung.model.ListFilmResponse
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "ApiProvider"
    val listFilm : ArrayList<FilmModel> = ArrayList()
    var filmAdapter : FilmAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getListFilm()
        rv_film_list.layoutManager = LinearLayoutManager(this)
        filmAdapter = FilmAdapter(listFilm, this)
        rv_film_list.adapter = filmAdapter

        btnSignin.setOnClickListener{
            val intent = Intent(applicationContext, SigninActivity::class.java)
            startActivity(intent)
        }

        btnUpload.setOnClickListener{
            val intent = Intent(applicationContext, Upload::class.java)
            startActivity(intent)
        }

    }

    //get List Phim
    fun getListFilm (){
            ApiProvider().callApiGet(object : ApiResult {
                override fun onError(e: Exception) {
                    Toast.makeText(applicationContext,"Không thể lấy được phim",Toast.LENGTH_SHORT).show()
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
    // Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.btnSearch -> {
                val searchView: SearchView = item?.actionView as SearchView
                searchView.onActionViewExpanded()
                searchView.requestFocus()
                val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
                searchView.setOnSearchClickListener {
                    //loadQuery("null")
                }
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            //if (query.isNotEmpty()) loadQuery("%$query%")
                            //if (query.isEmpty()) loadQuery("null")
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            //if (newText.length > 1) loadQuery("%$newText%")
                            //if (newText.isEmpty()) loadQuery("null")
                        }
                        return false
                    }
                })
                searchView.setOnCloseListener {
                    //loadQuery("%")
                    false
                }
                //presenter.onDrawerOptionAboutClick()
                return true
            }
            else -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    //khi quay lại sẽ get lại list film
    override fun onResume() {
        super.onResume()
        getListFilm()
    }
}
