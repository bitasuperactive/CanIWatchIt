package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * Información general sobre las plataformas de streaming en que un título se encuentra disponible.
 *
 * @param id Identificador único en la api
 * @param type Tipo de acceso al contenido (suscripción/compra)
 * @param region Regiones para las que el título se encuentra disponible
 * @param titleUrl Enlace del título en una plataforma de streaming específica
 */
data class TitleStreamingSource(
    @SerializedName("source_id")
    val id: Int?,
    val name: String?,
    val type: String?,
    val region: String?,
    @SerializedName("web_url")
    val titleUrl: String?,
    var logoUrl: String?,
)