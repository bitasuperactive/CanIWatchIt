package com.example.caniwatchitapplication.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity

@Dao
interface SubscribedStreamingSourcesDao
{
    /**
     * Inserta o reemplaza una plataforma de streaming suscrita.
     * @return Identificador único del objeto insertado o reemplazado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streamingSource: SubscribedStreamingSourceEntity): Long

    /**
     * Recupera todas las plataformas de streaming suscritas por el usuario.
     */
    @Query("SELECT * FROM subscribed_streaming_sources")
    fun getAll(): LiveData<List<SubscribedStreamingSourceEntity>>

    /**
     * Elimina una plataforma de streaming suscrita.
     */
    @Delete
    suspend fun delete(streamingSource: SubscribedStreamingSourceEntity)
}