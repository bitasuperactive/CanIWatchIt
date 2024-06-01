package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa la respuesta de la api a la consulta de los detalles de un título.
 *
 * @param id Identificador único en la api
 * @param imdbId Identificador único en IMDb
 * @param posterUrl Url del poster (2:3 píxeles)
 * @param title Nombre comercial
 * @param year Año de estreno
 * @param userRating Media de la puntuación de los usuarios en IMDb, sobre 10
 * @param plotOverview Sinopsis
 * @param streamingSources Lista de las plataformas de streaming en que se encuentra disponible
 */
data class TitleDetailsResponse(
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
    @SerializedName("poster")
    val posterUrl: String,
    @SerializedName("original_title")
    val title: String,
    val year: Int,
    @SerializedName("user_rating")
    val userRating: Double,
    @SerializedName("plot_overview")
    val plotOverview: String,
    @SerializedName("sources")
    val streamingSources: List<TitleStreamingSource>,
)