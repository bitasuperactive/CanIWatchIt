package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

data class Title(
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
    val name: String,
    val year: Int
)