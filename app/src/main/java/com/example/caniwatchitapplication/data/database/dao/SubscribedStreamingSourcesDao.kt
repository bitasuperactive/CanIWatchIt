package com.example.caniwatchitapplication.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity

/**
 * Room Data Access Object de las plataformas de streaming suscritas por el usuario.
 */
@Dao
interface SubscribedStreamingSourcesDao
{
    /**
     * Inserta o actualiza una plataforma de streaming suscrita.
     *
     * @param streamingSource Plataforma de streaming a insertar o actualizar
     *
     * @return Identificador único de Room del objeto insertado o reemplazado.
     */
    @Upsert
    suspend fun upsert(streamingSource: SubscribedStreamingSourceEntity): Long

    /**
     * Recupera todas las plataformas de streaming suscritas por el usuario.
     */
    @Query("SELECT * FROM subscribed_streaming_sources")
    fun getAll(): LiveData<List<SubscribedStreamingSourceEntity>>

    /**
     * Elimina una plataforma de streaming suscrita.
     *
     * @param streamingSource Plataforma de streaming a eliminar
     */
    @Delete
    suspend fun delete(streamingSource: SubscribedStreamingSourceEntity)
}