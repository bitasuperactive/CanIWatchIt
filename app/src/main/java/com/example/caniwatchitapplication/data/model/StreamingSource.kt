package com.example.caniwatchitapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// TODO - Rename table to "subscribed_streaming_sources".
@Entity("subscribed_services")
data class StreamingSource(
    @PrimaryKey(false)
    val id: Int,
    val name: String,
    @SerializedName("logo_100px")
    val logo100pxUrl: String,
)