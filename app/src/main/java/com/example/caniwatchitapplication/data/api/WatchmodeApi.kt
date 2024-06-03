package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.data.model.QuotaResponse
import com.example.caniwatchitapplication.data.model.StreamingSourcesResponse
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.data.model.TitlesIdsResponse
import com.example.caniwatchitapplication.util.Constants.Companion.API_KEY
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_REGIONS
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_TYPES
import com.example.caniwatchitapplication.util.Constants.Companion.TITLE_LANGUAGE
import com.example.caniwatchitapplication.util.Constants.Companion.TITLE_TYPES
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Watchmode - [API Docs](https://api.watchmode.com/docs/)
 */
interface WatchmodeApi
{
    /**
     * Recupera el número de peticiones realizadas y las contratadas por mes para una clave
     * específica.
     *
     * _No consume creditos adicionales de la api._
     */
    @GET("/v1/status/")
    suspend fun getQuota(
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<QuotaResponse>

    /**
     * Recupera todos los servicios de streaming disponibles para unos tipos de contenido y unas
     * regiones específicas.
     */
    @GET("/v1/sources/")
    suspend fun getAllStreamingSources(
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("types")
        types: String = STREAMING_SOURCE_TYPES,
        @Query("regions")
        regions: String = STREAMING_SOURCE_REGIONS
    ): Response<StreamingSourcesResponse>

    /**
     * Recupera los identificadores correspondientes a los títulos coincidentes con un valor
     * de búsqueda.
     *
     * @param searchValue Nombre original de los títulos a recuperar
     */
    @GET("/v1/search/")
    suspend fun searchForTitles(
        @Query("search_value")
        searchValue: String,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("search_field")
        searchField: String = "name",
        @Query("types")
        types: String = TITLE_TYPES
    ): Response<TitlesIdsResponse>

    /**
     * Recupera los detalles de un título específico mediante su identificador único.
     *
     * @param titleId Identificador único del título a recuperar
     */
    @GET("/v1/title/{title_id}/details/")
    suspend fun getTitleDetails(
        @Path("title_id")
        titleId: String,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("append_to_response")
        appendToResponse: String = "sources",
        @Query("language")
        language: String = TITLE_LANGUAGE
    ): Response<TitleDetailsResponse>
}