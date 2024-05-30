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
    @SerializedName("sources")
    val streamingSourcesIds: List<TitleStreamingSource>,
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
        if (poster != other.poster) return false
        if (title != other.title) return false
        if (year != other.year) return false
        if (userRating != other.userRating) return false
        if (plotOverview != other.plotOverview) return false
        if (streamingSourcesIds != other.streamingSourcesIds) return false

        return true
    }
}