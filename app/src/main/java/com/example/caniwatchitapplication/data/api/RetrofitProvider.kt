package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Proporciona una instancia única de la api.
 * Retrofit utiliza GsonConverterFactory para la conversión de los Json devueltos por la api,
 * además de implementar un HttpLoggingInterceptor para loguear las respuestas del endpoint.
 */
class RetrofitProvider
{
    companion object
    {
        val api: WatchmodeApi by lazy {

            retrofit.create(WatchmodeApi::class.java)
        }

        private val retrofit: Retrofit by lazy {
            
            val interceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
            
            Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}