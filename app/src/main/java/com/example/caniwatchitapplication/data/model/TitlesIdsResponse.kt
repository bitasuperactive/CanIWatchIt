package com.example.caniwatchitapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa la respuesta de la api a una búsqueda de títulos.
 *
 * @param titleIds Lista de los identificadores de los títulos coincidentes con la búsqueda
 */
data class TitlesIdsResponse(
    @SerializedName("title_results")
    val titleIds: List<TitleIds>
)
