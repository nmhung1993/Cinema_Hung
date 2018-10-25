package com.apitiny.administrator.hungcinema.model

class FilmModel : BaseModel {
  var _id: String? = null
  var posterURL: String? = null
  var name: String? = null
  var genre: String? = null
  var releaseDate: String? = null
  var content: String? = null
  var creatorId: String? = null
  var user: User? = null
}

class ListFilmResponse : BaseModel {
  var films = listOf<FilmModel>()
}

class FilmDetailModel : BaseModel {
  var cinema = FilmModel()
}