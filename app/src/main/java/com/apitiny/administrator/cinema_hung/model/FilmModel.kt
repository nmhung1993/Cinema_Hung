package com.apitiny.administrator.cinema_hung.model

class FilmModel : BaseModel {
    var posterURL: String? = null
    var name: String? = null
    var genre: String? = null
    var releaseDate: Long? = null
    var content: String? = null
    var creatorId: String? = null
    var user: User ? = null
}

class ListFilmResponse : BaseModel {
    var films = listOf<FilmModel>()
}