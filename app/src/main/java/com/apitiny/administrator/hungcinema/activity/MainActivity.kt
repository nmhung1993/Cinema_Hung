package com.apitiny.administrator.hungcinema.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.adapter.FilmAdapter
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.BaseModel
import com.apitiny.administrator.hungcinema.model.FilmModel
import com.apitiny.administrator.hungcinema.model.ListFilmResponse
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {
  
  private lateinit var imm: InputMethodManager
  
  private lateinit var aDialog: AwesomeProgressDialog
  
  private var prefValue = PreferencesHelper(this)
  private val tag = "ApiProvider"
  private var listFilmSearch: ArrayList<FilmModel> = ArrayList()
  val listFilm: ArrayList<FilmModel> = ArrayList()
  var filmAdapter: FilmAdapter? = null
  
  private lateinit var mHandler: Handler
  private lateinit var mRunnable: Runnable
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    hideSoftKeyboard()
    
    imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
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
    
    getListFilm()
    rv_film_list.layoutManager = LinearLayoutManager(this)
    filmAdapter = FilmAdapter(listFilm, this)
    rv_film_list.adapter = filmAdapter
    
    val resId = R.anim.layout_animation_fall_down
    val animation = AnimationUtils.loadLayoutAnimation(this, resId)
    rv_film_list.layoutAnimation = animation
    
    if (prefValue.getVal(application, "token") == null)
      btnProfile.visibility = View.INVISIBLE
    else btnProfile.visibility = View.VISIBLE
    
    btnProfile.setOnClickListener {
      val intent = Intent(applicationContext, ProfileActivity::class.java)
      startActivity(intent)
      overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }
    
    btnUpload.setOnClickListener {
      if (prefValue.getVal(application, "token") == null) showDialog()
      else {
        val intent = Intent(applicationContext, UploadActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
      }
      
    }
    
    mHandler = Handler()
    swipe_refresh_layout.setOnRefreshListener {
      mRunnable = Runnable {
        getListFilm()
        swipe_refresh_layout.isRefreshing = false
      }
      mHandler.postDelayed(mRunnable, 3000)
    }
    
  }
  
  //get List Phim
  private fun getListFilm() {
    ApiProvider().callApiGetFilmList(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e(tag, e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ListFilmResponse) {
          listFilm.clear()
          listFilm.addAll(baseModel.films)
          filmAdapter?.notifyDataSetChanged()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e(tag, "Received a different model")
      }
      
      override fun onAPIFail() {
        Log.e(tag, "Failed horribly")
      }
    })
  }
  
  fun filter(inputText: String?) {
    var inputText = inputText
    listFilmSearch.clear()
    if (inputText == null || inputText == "") {
      listFilmSearch.addAll(listFilm)
    } else {
      inputText = inputText.toLowerCase(Locale.getDefault())
      for (item in listFilm) {
        if (item.name != null) {
          if (item.name?.toLowerCase()!!.contains(inputText.toLowerCase()) || item.name!!.contains(inputText)) {
            listFilmSearch.add(item)
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
      showSoftKeyboard(searchView)
      searchView.onActionViewExpanded()
      searchView.requestFocus()
      searchView.queryHint = searchHint
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
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
    val aiDialog = AwesomeInfoDialog(this)
        .setTitle("ĐĂNG NHẬP")
        .setMessage("Bạn có muốn đăng nhập không?")
        .setPositiveButtonText("Đăng Nhập")
        .setColoredCircle(R.color.colorPrimary)
        .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
        .setPositiveButtonbackgroundColor(R.color.colorPrimary)
        .setPositiveButtonTextColor(R.color.white)
        .setCancelable(true)
        .setPositiveButtonClick({
                                  val intent = Intent(applicationContext, SigninActivity::class.java)
                                  startActivity(intent)
                                  overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
                                })
    aiDialog.show()
  }
  
  private fun hideSoftKeyboard() {
    if (currentFocus != null) {
      val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
  }
  
  private fun showSoftKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, 0)
  }
  
  override fun onResume() {
    super.onResume()
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    getListFilm()
    if (prefValue.getVal(application, "token") == null)
      btnProfile.visibility = View.INVISIBLE
    else btnProfile.visibility = View.VISIBLE
  }
}