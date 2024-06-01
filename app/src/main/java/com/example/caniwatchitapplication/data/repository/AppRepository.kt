package com.example.caniwatchitapplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.model.QuotaResponse
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.data.model.TitleIds
import com.example.caniwatchitapplication.util.Constants
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations.Companion.toAvailableEntityList
import com.example.caniwatchitapplication.util.Transformations.Companion.toModelList
import com.example.caniwatchitapplication.util.Transformations.Companion.toSubscribedEntity
import java.io.IOException
import java.time.LocalDate
import java.time.Period

/**
 * Recupera los datos requeridos por el ViewModel, gestiona las respuestas del endpoint y realiza
 * las transformaciones necesarias actuando como puente entre la base de datos, la api y el
 * ViewModel.
 *
 * @param database Base de datos Room de la aplicación
 *
 * @see AppDatabase
 */
class AppRepository(
    private val database: AppDatabase
)
{
    private val tag = "AppRepository"

    /**
     * Imprime en consola el número de peticiones realizadas a la api y el número de peticiones
     * disponibles por mes natural.
     *
     * @see getApiQuota
     */
    suspend fun logApiQuota()
    {
        try
        {
            when (val resource = getApiQuota()) {

                is Resource.Success -> {
                    resource.data?.let {
                        Log.d(tag, "Requests used (Api quota): ${it.quotaUsed}/${it.quota}")
                    }
                }
                else -> Log.d(tag, "Error on logging api quota: ${resource.message}")
            }

        } catch (t: Throwable)
        {
            when (t)
            {
                is IOException -> Log.d(tag, "Exception on logging api quota: Network failure")
                else -> Log.d(tag, "Error on logging api quota: Json to Kotlin conversion failure")
            }
        }
    }

    /**
     * Recupera las plataformas de streaming disponibles a ser suscritas por el usuario de la
     * base de datos o, si se ha cumplido el plazo prestablecido, la actualiza recuperando los datos
     * de la api.
     *
     * @see Constants.DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES
     * @see com.example.caniwatchitapplication.data.database.dao.AvailableStreamingSourcesDao.getAll
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getAllStreamingSources
     */
    suspend fun getAllStreamingSources(): Resource<List<StreamingSource>>
    {
        // Fetch from database
        val availableStreamingSources = database.getAvailableStreamingSourcesDao().getAll()

        if (availableStreamingSources.isNotEmpty()) {
            // Check if the data is 24h old to update it
            val lastUpdated = availableStreamingSources[0].lastUpdated
            if (!updateIsNeeded(lastUpdated, Constants.DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES)) {
                return Resource.Success(availableStreamingSources.toModelList())
            }
        }

        // Fetch from api
        val response = RetrofitProvider.api.getAllStreamingSources()

        if (response.isSuccessful) {

            response.body()?.let {
                // Store available streaming sources in Room
                database.getAvailableStreamingSourcesDao().upsertAll(it.toAvailableEntityList())
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }

    /**
     * Recupera los detalles de los títulos resultantes de la búsqueda del usuario.
     *
     * @param searchValue Nombre original de los títulos a recuperar
     *
     * @return Recurso con los detalles de los títulos coincidentes.
     *
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.searchForTitles
     * @see getTitlesDetails
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
        database.getSubscribedStreamingSourcesDao().getAll().map {
            it.toModelList()
        }

    /**
     * @see com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao.upsert
     */
    suspend fun upsertSubscribedStreamingSource(
        streamingSource: StreamingSource
    ): Long = database.getSubscribedStreamingSourcesDao().upsert(streamingSource.toSubscribedEntity())

    /**
     * @see com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao.delete
     */
    suspend fun deleteSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = database.getSubscribedStreamingSourcesDao().delete(streamingSource.toSubscribedEntity())

    /**
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getQuota
     */
    private suspend fun getApiQuota(): Resource<QuotaResponse>
    {
        val response = RetrofitProvider.api.getQuota()

        if (response.isSuccessful) {

            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }

    /**
     * Comprueba si se ha cumplido el plazo especificado para actualizar la base de datos.
     *
     * @param lastUpdated Fecha de la última actualización
     * @param daysToUpdate Número de días a esperar para actualizar los datos
     *
     * @return Verdadero si es necesario actualizar, falso en su defecto
     */
    private fun updateIsNeeded(lastUpdated: LocalDate, daysToUpdate: Int): Boolean
    {
        val periodFromLastUpdate = Period.between(lastUpdated, LocalDate.now())
        return periodFromLastUpdate.days >= daysToUpdate
    }

    /**
     * Recupera los detalles de los títulos especificados.
     *
     * _El número de peticiones se encuentra limitado por la correspondiente constante para evitar
     * excesos de llamadas a la api._
     *
     * @param titleIds Lista de identificadores para los títulos a recuperar
     *
     * @return Recurso con los detalles del título especificado.
     *
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