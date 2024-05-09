package com.example.caniwatchitapplication.data.api

import com.example.caniwatchitapplication.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider
{
    companion object
    {
        private val retrofit: Retrofit by lazy {
            
            val interceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
            
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        
        val api: WatchmodeApi by lazy {
            
            retrofit.create(WatchmodeApi::class.java)
        }
    }
}