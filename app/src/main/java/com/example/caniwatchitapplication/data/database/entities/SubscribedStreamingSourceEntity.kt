package com.example.caniwatchitapplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de una plataforma de streaming suscrita por el usuario.
 *
 * @see StreamingSourceEntity
 */
@Entity("subscribed_streaming_sources")
data class SubscribedStreamingSourceEntity(
    @PrimaryKey(false)
    override val id: Int,
    override val name: String,
    override val logoUrl: String,
) : StreamingSourceEntity