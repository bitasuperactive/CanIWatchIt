package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.util.Constants.Companion.APP_REPOSITORY_BASE_URL
import com.example.caniwatchitapplication.util.Constants.Companion.PANTRY_API_BASE_URL
import com.example.caniwatchitapplication.util.Constants.Companion.WATCHMODE_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Proporciona una instancia única para las apis de la aplicación.
 */
class RetrofitProvider
{
    companion object
    {
        val watchmodeApi: WatchmodeApi by lazy {
            build(WATCHMODE_API_BASE_URL).create(WatchmodeApi::class.java)
        }

        val githubApi: GithubApi by lazy {
            build(APP_REPOSITORY_BASE_URL).create(GithubApi::class.java)
        }

        val pantryApi: PantryApi by lazy {
            build(PANTRY_API_BASE_URL).create(PantryApi::class.java)
        }

        /**
         * Construye una instancia de Retrofit utilizando GsonConverterFactory para la conversión
         * de los Json devueltos por la api, además de implementar un HttpLoggingInterceptor para
         * loguear las respuestas del endpoint.
         *
         * @see buildClient
         */
        private fun build(baseUrl: String): Retrofit
        {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(buildClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        /**
         * Cliente interceptor a nivel de cuerpo con un timeout general de 30 segundos.
         */
        private fun buildClient(): OkHttpClient
        {
            val interceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }
    }
}