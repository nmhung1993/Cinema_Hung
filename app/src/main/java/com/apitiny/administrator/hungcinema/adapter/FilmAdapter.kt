package com.apitiny.administrator.hungcinema.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.activity.FilmDetailActivity
import com.apitiny.administrator.hungcinema.model.FilmModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.film_item.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FilmAdapter(var items: ArrayList<FilmModel>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.film_item, parent, false))
    
  }
  
  override fun getItemCount(): Int {
    return items.size
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
    var milisec: String = items.get(position).releaseDate.toString()
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
    
    if (items.get(position).posterURL != null || items.get(position).posterURL != "null") {
      Glide.with(context)
          .load("https://cinema-hatin.herokuapp.com" + items.get(position).posterURL)
          .apply(RequestOptions().placeholder(R.drawable.ic_defaultmv))
          .into(holder.img)
    }
    
    holder?.name?.text = items.get(position).name
    holder?.genre?.text = items.get(position).genre
    holder?.releaseDate?.text = dateString
    if (items.get(position).user?.name == null)
      holder?.creatorId?.text = "Unknown"
    else holder?.creatorId?.text = items.get(position).user?.name
    
    holder?.rView.setOnClickListener {
      val intent = Intent(context, FilmDetailActivity::class.java)
      intent.putExtra("_id", items.get(position)._id)
      context.startActivity(intent)
      //            Toast.makeText(context, items.get(position)._id, Toast.LENGTH_SHORT).show()
    }
    
    //        holder?.containerView?.setOnClickListener { clickListener(item) }
  }
  
  
  fun setSearchResult(result: ArrayList<FilmModel>) {
    items = result
    notifyDataSetChanged()
  }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  
  private var view: View = view
  
  val img = view.imgFilm
  val name = view.name
  val genre = view.genre
  val releaseDate = view.releaseDate
  val creatorId = view.creatorName
  val rView = view.item_detail
}
