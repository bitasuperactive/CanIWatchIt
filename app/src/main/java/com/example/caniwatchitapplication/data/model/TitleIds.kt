package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa los identificadores de un título.
 *
 * @param id Identificador único en la api
 * @param imdbId Identificador único en IMDb
 */
data class TitleIds(
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
)