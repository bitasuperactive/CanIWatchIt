package com.example.caniwatchitapplication.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.model.watchmode.StreamingSource
import com.example.caniwatchitapplication.data.model.watchmode.TitleDetailsResponse
import com.example.caniwatchitapplication.data.model.watchmode.TitleIds
import com.example.caniwatchitapplication.util.Constants
import com.example.caniwatchitapplication.util.Constants.Companion.DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations.Companion.toAvailableEntityList
import com.example.caniwatchitapplication.util.Transformations.Companion.toModelList
import com.example.caniwatchitapplication.util.Transformations.Companion.toSubscribedEntity
import java.time.LocalDate
import java.time.Period

/**
 * Recupera los datos requeridos por el ViewModel, gestiona las respuestas de los endpoints y
 * realiza las transformaciones necesarias actuando como puente entre la base de datos, las apis y
 * el ViewModel.
 *
 * @param database Base de datos Room de la aplicación
 *
 * @see AppDatabase
 */
class WatchmodeRepository(
    private val database: AppDatabase
)
{
    /**
     * Filtra la lista de claves api para Watchmode en base al número de peticiones restantes.
     *
     * Comprueba que el Endpoint Watchmode se encuentran disponible y garantiza
     * una lista de claves api poblada exclusivamente con aquellas que tengan disponibles un mínimo
     * de 20 peticiones, en cuyo defecto devolverá una lista vacía.
     *
     * @param keyList Lista de claves api para Watchmode
     *
     * @return Lista de claves api con 20 o más peticiones disponibles
     *
     * @throws Throwable Propaga todos los errores.
     *
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getQuota
     */
    suspend fun filterApiKeys(keyList: List<String>): List<String>
    {
        val result = ArrayList<String>()

        for (key: String in keyList) {
            val response = RetrofitProvider.watchmodeApi.getQuota(key)

            if (response.isSuccessful) {
                response.body()?.let {
                    val quotaLeft = it.quota?.minus(it.quotaUsed ?: it.quota)

                    if (quotaLeft != null && quotaLeft >= 20) {
                        result.add(key)
                    }
                }
            }
        }

        return result
    }

    /**
     * Recupera las plataformas de streaming disponibles a ser suscritas por el usuario de la
     * base de datos o, si se ha cumplido el plazo prestablecido, la actualiza recuperando los datos
     * de la api.
     *
     * @see updateIsNeeded
     * @see com.example.caniwatchitapplication.data.database.dao.AvailableStreamingSourcesDao.getAll
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getAllStreamingSources
     */
    suspend fun getAllStreamingSources(apiKey: String): Resource<List<StreamingSource>>
    {
        // Fetch from database
        val availableStreamingSources = database.getAvailableStreamingSourcesDao().getAll()

        if (availableStreamingSources.isNotEmpty()) {
            // Check if the data is 24h old to update it
            val lastUpdated = availableStreamingSources[0].lastUpdated
            if (!updateIsNeeded(lastUpdated)) {
                return Resource.Success(availableStreamingSources.toModelList())
            }
        }

        // Fetch from api
        val response = RetrofitProvider.watchmodeApi.getAllStreamingSources(apiKey)

        if (response.isSuccessful) {

            response.body()?.let {
                // Store available streaming sources in database
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
    suspend fun searchForTitles(apiKey: String, searchValue: String): Resource<List<TitleDetailsResponse>>
    {
        val response = RetrofitProvider.watchmodeApi.searchForTitles(apiKey, searchValue)

        if (response.isSuccessful) {

            response.body()?.let {
                return getTitlesDetails(it.titleIds, apiKey)
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
     * Recupera los detalles de los títulos especificados.
     *
     * _El número de peticiones a la api se encuentra limitado por la correspondiente constante
     * para controlar la cuota._
     *
     * @param titleIds Lista de identificadores para los títulos a recuperar
     *
     * @return Recurso con los detalles del título especificado.
     *
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getTitleDetails
     */
    private suspend fun getTitlesDetails(
        titleIds: List<TitleIds>,
        apiKey: String
    ): Resource<List<TitleDetailsResponse>>
    {
        val result = ArrayList<TitleDetailsResponse>(titleIds.size)

        // Calls limited by the following constant.
        for (title in titleIds.take(Constants.MAX_TITLE_DETAILS_REQUESTS))
        {
            val response = RetrofitProvider.watchmodeApi.getTitleDetails(title.id.toString(), apiKey)
            val titleDetails = response.body()

            if (response.isSuccessful && titleDetails != null) {

                result.add(titleDetails)
                continue
            }

            return Resource.Error(response.message(), result)
        }

        return Resource.Success(result)
    }

    /**
     * Comprueba si se ha cumplido el plazo predefinido para actualizar las plataformas de
     * streaming disponibles en la base de datos Room.
     *
     * @param lastUpdated Fecha de la última actualización
     *
     * @return Verdadero si es necesario actualizar, falso en su defecto
     */
    private fun updateIsNeeded(lastUpdated: LocalDate): Boolean
    {
        val periodFromLastUpdate = Period.between(lastUpdated, LocalDate.now())
        return periodFromLastUpdate.days >= DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES
    }
}