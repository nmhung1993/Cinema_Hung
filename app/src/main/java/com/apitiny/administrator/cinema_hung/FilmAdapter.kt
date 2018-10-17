package com.apitiny.administrator.cinema_hung

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apitiny.administrator.cinema_hung.model.FilmModel
import kotlinx.android.synthetic.main.film_item.view.*

class FilmAdapter(val items : ArrayList<FilmModel>, val context: Context) : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.film_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.img?.setImageResource(items.get(position))
        holder?.name?.text = items.get(position).name
        holder?.genre?.text = items.get(position).genre
        holder?.releaseDate?.text = items.get(position).releaseDate.toString()
        holder?.creatorId?.text = items.get(position).user?.name
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    //val img = view.imgFilm
    val name = view.name
    val genre = view.genre
    val releaseDate = view.releaseDate
    val creatorId = view.creatorId
}