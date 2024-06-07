package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Clase de datos que representa los identificadores de un título.
 *
 * @param id Identificador único en la api
 * @param imdbId Identificador único en IMDb
 */
data class TitleIds(
    val id: Int?,
    @SerializedName("imdb_id")
    val imdbId: String?,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TitleIds

        if (id != other.id) return false
        if (imdbId != other.imdbId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + imdbId.hashCode()
        return result
    }
}