package com.example.caniwatchitapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.caniwatchitapplication.data.database.entities.AvailableStreamingSourceEntity

/**
 * Room Data Access Object para las plataformas disponibles para ser suscritas por el usuario.
 */
@Dao
interface AvailableStreamingSourcesDao
{
    /**
     * Inserta o reemplaza una lista de plataformas de streaming disponibles.
     *
     * @param availableStreamingSources Lista de plataformas de streaming a insertar o actualizar
     *
     * @return Lista de identificadores únicos de Room para los objetos insertados o reemplazados.
     */
    @Upsert
    suspend fun upsertAll(
        availableStreamingSources: List<AvailableStreamingSourceEntity>
    ): List<Long>

    /**
     * Recupera todas las plataformas de streaming disponibles.
     */
    @Query("SELECT * FROM available_streaming_sources")
    suspend fun getAll(): List<AvailableStreamingSourceEntity>
}