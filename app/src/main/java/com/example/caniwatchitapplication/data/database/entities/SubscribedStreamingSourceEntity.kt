package com.example.caniwatchitapplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clase de datos que representa la entidad de una plataforma de streaming para Room.
 *
 * @param id Identificador único en la api
 * @param name Nombre comercial
 * @param logoUrl Url del logo (100x100 píxeles)
 */
@Entity("subscribed_streaming_sources")
data class SubscribedStreamingSourceEntity(
    @PrimaryKey(false)
    val id: Int,
    val name: String,
    val logoUrl: String,
)