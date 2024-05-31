package com.example.caniwatchitapplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.StreamingSourcesResponse
import com.example.caniwatchitapplication.data.model.TitleIds
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.util.Constants
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations.Companion.toEntity
import com.example.caniwatchitapplication.util.Transformations.Companion.toModelList

/**
 * Gestiona las respuestas del endpoint y realiza las transformaciones necesarias para que el
 * ViewModel disponga directamente de los datos esperados.
 */
class AppRepository(
    private val db: AppDatabase
)
{
    private val tag = "AppRepository"

    /**
     * Imprime en consola las peticiones realizadas y, las contratadas por mes, para la clave en uso.
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getQuota
     */
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

    /**
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getAllStreamingSources
     */
    suspend fun getAllStreamingSources(): Resource<StreamingSourcesResponse>
    {
        val response = RetrofitProvider.api.getAllStreamingSources()

        if (response.isSuccessful) {

            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }

    /**
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.searchForTitles
     */
    suspend fun searchForTitles(searchValue: String): Resource<List<TitleDetailsResponse>>
    {
        val response = RetrofitProvider.api.searchForTitles(searchValue)

        if (response.isSuccessful) {

            response.body()?.let {
                return getTitlesDetails(it.titleIds)
            }
        }

        return Resource.Error(response.message(), null)
    }

    /**
     * @see com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao.getAll
     */
    fun getAllSubscribedStreamingSources(): LiveData<List<StreamingSource>> =
        db.getSubscribedStreamingSourcesDao().getAll().map {
            it.toModelList()
        }

    /**
     * @see com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao.upsert
     */
    suspend fun upsertSubscribedStreamingSource(
        streamingSource: StreamingSource
    ): Long = db.getSubscribedStreamingSourcesDao().upsert(streamingSource.toEntity())

    /**
     * @see com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao.delete
     */
    suspend fun deleteSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = db.getSubscribedStreamingSourcesDao().delete(streamingSource.toEntity())

    /**
     * Recupera los detalles de los títulos por medio de una lista de identificadores.
     * El número de peticiones se encuentra limitado por la correspondiente constante para evitar
     * excesos de llamadas a la api.
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getTitleDetails
     */
    private suspend fun getTitlesDetails(titleIds: List<TitleIds>): Resource<List<TitleDetailsResponse>>
    {
        val result = ArrayList<TitleDetailsResponse>(titleIds.size)

        // Calls limited by the following constant.
        for (title in titleIds.take(Constants.MAX_TITLE_DETAILS_REQUESTS))
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