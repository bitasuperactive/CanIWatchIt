package com.example.caniwatchitapplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.db.AppDatabase
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.StreamingSourcesResponse
import com.example.caniwatchitapplication.data.model.Title
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.util.Constants
import com.example.caniwatchitapplication.util.Resource

class AppRepository(
    private val db: AppDatabase
)
{
    private val tag = "AppRepository"

    suspend fun logQuota()
    {
        val response = RetrofitProvider.api.getQuota()

        if (response.isSuccessful) {

            response.body()?.let {
                Log.d(tag, "Requests used: ${it.quotaUsed}/${it.quota}")
                return
            }
        }

        Log.d(tag, "An error occurred retrieving the apis quota: ${response.message()}")
    }

    suspend fun getAllSubscriptionStreamingSources(): Resource<StreamingSourcesResponse>
    {
        val response = RetrofitProvider.api.getAllStreamingSources()

        if (response.isSuccessful) {

            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }
    
    suspend fun searchForTitles(searchValue: String): Resource<List<TitleDetailsResponse>>
    {
        val response = RetrofitProvider.api.searchForTitles(searchValue)

        if (response.isSuccessful) {

            response.body()?.let {
                return getTitlesDetails(it.titles)
            }
        }

        return Resource.Error(response.message(), null)
    }

    fun getAllSubscribedStreamingSources(): LiveData<List<StreamingSource>> =
        db.getSubscribedStreamingSourcesDao().getAllSubscribedStreamingSources()
    
    suspend fun upsertSubscribedStreamingSource(streamingSource: StreamingSource): Long =
        db.getSubscribedStreamingSourcesDao().upsert(streamingSource)
    
    suspend fun deleteSubscribedStreamingSource(streamingSource: StreamingSource) =
        db.getSubscribedStreamingSourcesDao().delete(streamingSource)

    private suspend fun getTitlesDetails(titles: List<Title>): Resource<List<TitleDetailsResponse>>
    {
        val result = ArrayList<TitleDetailsResponse>(titles.size)

        // Calls limited by the following constant.
        for (title in titles.take(Constants.MAX_TITLE_DETAILS_REQUESTS))
        {
            val response = RetrofitProvider.api.getTitleDetails(title.id.toString())
            val titleDetails = response.body()

            if (response.isSuccessful && titleDetails != null) {

                result.add(titleDetails)
                continue
            }

            return Resource.Error(response.message(), result)
        }

        return Resource.Success(result)
    }
}