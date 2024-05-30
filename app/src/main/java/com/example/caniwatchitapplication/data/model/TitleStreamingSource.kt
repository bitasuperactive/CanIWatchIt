package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

data class TitleStreamingSource(
    @SerializedName("source_id")
    val id: Int,
    val type: String,
    val region: String,
)