package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.data.model.pantry.ApiKeysResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Pantry - [Api Docs](https://documenter.getpostman.com/view/3281832/SzmZeMLC)
 *
 * Base de datos remota de la aplicación.
 */
interface PantryApi
{
    /**
     * Recupera todas las claves api disponibles para la aplicación desde la base de datos remota.
     */
    @Headers("Content-Type: application/json")
    @GET("/apiv1/pantry/PANTRY_ID/basket/apiKeys")
    suspend fun getAllApiKeys (): Response<ApiKeysResponse>
}