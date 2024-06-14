package com.example.caniwatchitapplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad de una plataforma de streaming disponible en la api de Watchmode.
 *
 * @param lastUpdated Fecha de la última actualización de los datos
 *
 * @see StreamingSourceEntity
 */
@Entity("available_streaming_sources")
data class AvailableStreamingSourceEntity(
    @PrimaryKey(false)
    override val id: Int,
    override val name: String,
    override val logoUrl: String,
    val lastUpdated: LocalDate
) : StreamingSourceEntity