package com.example.caniwatchitapplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad para las peticiones realizadas a la api de Watchmode. Limitado a 1 entrada.
 */
@Entity("queries")
data class QueryEntity(
    @PrimaryKey(false)
    val id: Int = 0,

    /**
     * Número de peticiones realizadas.
     */
    val count: Int,

    /**
     * Fecha de registro.
     */
    val date: LocalDate = LocalDate.now(),
)