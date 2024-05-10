package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TitleDetailsResponse(
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
    val poster: String,
    @SerializedName("original_title")
    val title: String,
    val year: Int,
    @SerializedName("user_rating")
    val userRating: Double,
    @SerializedName("plot_overview")
    val plotOverview: String,
    val sources: List<TitleSource>,
) : Serializable
{
    override fun hashCode(): Int
    {
        return id.hashCode()
    }
}