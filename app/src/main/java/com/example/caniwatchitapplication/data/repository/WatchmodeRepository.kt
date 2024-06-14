package com.example.caniwatchitapplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.database.entities.QueryEntity
import com.example.caniwatchitapplication.data.model.watchmode.StreamingSource
import com.example.caniwatchitapplication.data.model.watchmode.TitleDetailsResponse
import com.example.caniwatchitapplication.data.model.watchmode.TitleIds
import com.example.caniwatchitapplication.util.Constants.Companion.DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES
import com.example.caniwatchitapplication.util.Constants.Companion.MAX_TITLE_DETAILS_REQUESTS
import com.example.caniwatchitapplication.util.Constants.Companion.QUERY_LIMIT_PER_DAY
import com.example.caniwatchitapplication.util.Constants.Companion.QUOTA_NEEDED_PER_DAY
import com.example.caniwatchitapplication.util.Constants.Companion.WATCHMODE_BACKUP_API_KEY
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations.Companion.toAvailableEntityList
import com.example.caniwatchitapplication.util.Transformations.Companion.toModelList
import com.example.caniwatchitapplication.util.Transformations.Companion.toSubscribedEntity
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

/**
 * Recupera los datos de la api Watchmode requeridos por el ViewModel y realiza las transformaciones
 * necesarias actuando como puente entre la base de datos, el Endpoint y el ViewModel.
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
     * Comprueba si se ha alcanzado el límite de búsquedas diarias y resetea el contador si han
     * pasado uno o más días.
     *
     * @see QUERY_LIMIT_PER_DAY
     */
    suspend fun isQueryLimitReached(): Boolean
    {
        if (QUERY_LIMIT_PER_DAY == -1) {
            return false
        }

        val queryEntity = database.getQueryDao().get() ?: return false
        val daysPassed = ChronoUnit.DAYS.between(queryEntity.date, LocalDate.now())

        if (daysPassed >= 1) {
            // Reset counter
            database.getQueryDao().upsert(QueryEntity(count = 0))
            return false
        }
        if (queryEntity.count < QUERY_LIMIT_PER_DAY) {
            return false
        }

        return true
    }

    /**
     * Añade 1 al número de búsquedas de títulos realizadas almacenado en la base de datos.
     */
    suspend fun addQueryCount()
    {
        val queryEntity = database.getQueryDao().get()
        val queryCount = (queryEntity?.count ?: 0) + 1
        database.getQueryDao().upsert(QueryEntity(count = queryCount))
    }

    /*
    TODO - Debería contabilizar todas las cuotas restantes en vez de las individuales.
     Habría que complementarlo con el sistema de seguimiento de las queries realizadas durante
     la sesión para recuperar siempre un clave api válida, en vez de una aleatoria que podría estar
     agotada.
     */
    /**
     * Filtra la lista de claves api para Watchmode en base al número de peticiones restantes. Si
     * la lista está vacía, utiliza la clave api de respaldo.
     *
     * Garantiza una lista de claves api poblada exclusivamente con aquellas que dispongan del
     * mínimo prestablecido de peticiones restantes, en cuyo defecto devolverá una lista vacía.
     *
     * El mínimo se peticiones requeridas se calcula en base a la cantidad diaria de peticiones
     * permitidas y al límite de resultados por búsqueda.
     *
     * @param keyList Lista de claves api para Watchmode
     *
     * @return Lista de claves api que disponen del mínimo de peticiones restantes requerido por día
     *
     * @see com.example.caniwatchitapplication.data.api.WatchmodeApi.getQuota
     * @see QUOTA_NEEDED_PER_DAY
     * @see WATCHMODE_BACKUP_API_KEY
     */
    suspend fun filterApiKeys(keyList: List<String>): List<String>
    {
        val keyList = keyList.ifEmpty { listOf(WATCHMODE_BACKUP_API_KEY) }
        var sumOfQuotas = 0
        val result = ArrayList<String>()

        for (key: String in keyList) {
            val response = RetrofitProvider.watchmodeApi.getQuota(key)

            if (response.isSuccessful) {
                response.body()?.let {
                    val quotaLeft = it.quota?.minus(it.quotaUsed ?: it.quota)

                    if (quotaLeft != null) {
                        sumOfQuotas += quotaLeft
                    }

                    // If the quota cannot be retrieved, add the key
                    if (quotaLeft == null || quotaLeft >= QUOTA_NEEDED_PER_DAY) {
                        result.add(key)
                    }
                }
            }
        }

        // Print sum of quotas left
        Log.d(this.javaClass.simpleName, "Quotas left: $sumOfQuotas")

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
        for (title in titleIds.take(MAX_TITLE_DETAILS_REQUESTS))
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