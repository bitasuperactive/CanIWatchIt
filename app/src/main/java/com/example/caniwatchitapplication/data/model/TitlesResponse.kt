package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

data class TitlesResponse(
    @SerializedName("title_results")
    val titles: List<Title>
)
