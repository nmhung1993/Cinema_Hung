package com.apitiny.administrator.cinema_hung.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.di.component.DaggerActivityComponent
import com.apitiny.administrator.cinema_hung.di.module.ActivityModule
import com.apitiny.administrator.cinema_hung.ui.about.AboutFragment
import com.apitiny.administrator.cinema_hung.ui.list.ListFragment
import javax.inject.Inject

//import android.support.v7.widget.SearchView
import android.app.SearchManager
import android.provider.ContactsContract
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener;

class MainActivity: AppCompatActivity(), MainContract.View {

    @Inject lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
                injectDependency()

        presenter.attach(this)

    }

    override fun onResume() {
        super.onResume()
        showUploadPage()
    }

    override fun showAboutFragment() {
        if (supportFragmentManager.findFragmentByTag(AboutFragment.TAG) == null) {
            //supportFragmentManager.beginTransaction()
                    //.addToBackStack(null)
                    //.setCustomAnimations(AnimType.FADE.getAnimPair().first, AnimType.FADE.getAnimPair().second)
                    //.replace(R.id.frame, AboutFragment().newInstance(), AboutFragment.TAG)
                    //.commit()
        } else {
            // Maybe an animation like shake hello text
        }
    }

    override fun showListFragment() {
        supportFragmentManager.beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(AnimType.SLIDE.getAnimPair().first, AnimType.SLIDE.getAnimPair().second)
                .replace(R.id.frame, ListFragment().newInstance(), ListFragment.TAG)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.search_button-> {
                //val searchView: SearchView = item?.actionView as SearchView
                val searchView: SearchView = item?.actionView as SearchView
                
                //auto expand và focus
                searchView.onActionViewExpanded()
                searchView.requestFocus()

                val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
                searchView.setOnSearchClickListener {
                    //loadQuery("null")
                }
                searchView.setOnQueryTextListener(object : OnQueryTextListener {

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
                    //return true
            }
            else -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(AboutFragment.TAG)

        if (fragment == null) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .build()

        activityComponent.inject(this)
    }

    private fun showUploadPage() {
        val uploadButton = findViewById<Button>(R.id.upload)
        uploadButton.setOnClickListener{
            val intent = Intent(applicationContext, Upload::class.java)
            startActivity(intent)
        }
    }

    enum class AnimType() {
        SLIDE,
        FADE;

        fun getAnimPair(): Pair<Int, Int> {
            when(this) {
                SLIDE -> return Pair(R.anim.slide_left, R.anim.slide_right)
                FADE -> return Pair(R.anim.fade_in, R.anim.fade_out)
            }

            return Pair(R.anim.slide_left, R.anim.slide_right)
        }
    }
}