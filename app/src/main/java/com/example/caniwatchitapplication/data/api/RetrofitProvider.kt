package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Proporciona una instancia única de las apis de la aplicación.
 */
class RetrofitProvider
{
    companion object
    {
        val watchmodeApi: WatchmodeApi by lazy {
            build(Constants.WATCHMODE_API_BASE_URL).create(WatchmodeApi::class.java)
        }

        /**
         * Construye una instancia de Retrofit utilizando GsonConverterFactory para la conversión
         * de los Json devueltos por la api, además de implementar un HttpLoggingInterceptor para
         * loguear las respuestas del endpoint.
         *
         * @see buildClient
         */
        fun build(baseUrl: String): Retrofit
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