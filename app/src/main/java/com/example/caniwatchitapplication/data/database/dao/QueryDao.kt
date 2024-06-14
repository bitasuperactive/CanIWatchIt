package com.example.caniwatchitapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.caniwatchitapplication.data.database.entities.QueryEntity

/**
 * Room Data Access Object para las peticiones de búsqueda a la api de Watchmode.
 */
@Dao
interface QueryDao
{
    /**
     * Inserta o actualiza las peticiones realizadas.
     */
    @Upsert
    suspend fun upsert(query: QueryEntity): Long

    /**
     * Recupera el número de peticiones realizadas.
     */
    @Query("SELECT * FROM queries WHERE id = 0")
    suspend fun get(): QueryEntity?
}