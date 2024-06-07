package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.data.model.AppVersionInfo
import retrofit2.Response
import retrofit2.http.GET

/**
 * Api para el repositorio de la aplicación en GitHub.
 */
interface GithubApi
{
    /**
     * Recupera el BuildConfig.json del último release.
     */
    @GET("releases/latest/download/BuildConfig.json")
    suspend fun getLatestRelease(): Response<AppVersionInfo>
}