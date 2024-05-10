package com.example.caniwatchitapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity("subscribed_services")
data class Service(
    @PrimaryKey(false)
    val id: Int,
    val name: String,
    @SerializedName("logo_100px")
    val logo100pxUrl: String,
)