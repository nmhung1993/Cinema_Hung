package com.apitiny.administrator.hungcinema.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.activity.FilmDetailActivity
import com.apitiny.administrator.hungcinema.model.FilmModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.myfilm_item.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MyFilmAdapter(private var items: ArrayList<FilmModel>, val context: Context) : RecyclerView.Adapter<ViewHolderMF>() {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMF {
    
    return ViewHolderMF(LayoutInflater.from(context).inflate(R.layout.myfilm_item, parent, false))
    
  }
  
  override fun getItemCount(): Int {
    return items.size
  }
  
  @SuppressLint("SimpleDateFormat")
  override fun onBindViewHolder(holder: ViewHolderMF, position: Int) {
    
    if (items.get(position).posterURL != null || items.get(position).posterURL != "null") {
      Glide.with(context)
          .load("https://cinema-hatin.herokuapp.com" + items.get(position).posterURL)
          .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
          .into(holder.img)
    }
    
    holder.name?.text = items.get(position).name
    holder.rView.setOnClickListener {
      val intent = Intent(context, FilmDetailActivity::class.java)
      intent.putExtra("_id", items[position]._id)
      context.startActivity(intent)
    }
    
  }
  
}

class ViewHolderMF(view: View) : RecyclerView.ViewHolder(view) {
  
  val img = view.myFilmimg
  val name = view.myFilmname
  val rView = view.myFilm_detail
}
