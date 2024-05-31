package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// TODO - ¿De dónde obtiene la api el rating de los usuarios?
/**
 * Clase de datos que representa la respuesta de la api a la consulta de los detalles de un título.
 *
 * @param id Identificador único en la api
 * @param imdbId Identificador único en IMDb
 * @param posterUrl Url del poster (2:3 píxeles)
 * @param title Nombre comercial
 * @param year Año de estreno
 * @param userRating Media de la puntuación de los usuarios sobre 10
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
) : Serializable
{
    override fun hashCode(): Int
    {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TitleDetailsResponse

        if (id != other.id) return false
        if (imdbId != other.imdbId) return false
        if (posterUrl != other.posterUrl) return false
        if (title != other.title) return false
        if (year != other.year) return false
        if (userRating != other.userRating) return false
        if (plotOverview != other.plotOverview) return false
        if (streamingSources != other.streamingSources) return false

        return true
    }
}