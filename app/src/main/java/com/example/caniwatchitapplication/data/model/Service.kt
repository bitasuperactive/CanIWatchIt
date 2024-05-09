package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

data class Service(
    val id: Int,
    val name: String,
    @SerializedName("logo_100px")
    val logo100pxUrl: String,
)