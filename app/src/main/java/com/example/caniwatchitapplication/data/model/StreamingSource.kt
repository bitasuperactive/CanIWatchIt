package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una plataforma de streaming.
 *
 * @param id Identificador único en la api
 * @param name Nombre comercial
 * @param logoUrl Url del logo (100x100 píxeles)
 */
data class StreamingSource(
    val id: Int?,
    val name: String?,
    @SerializedName("logo_100px")
    val logoUrl: String?,
)