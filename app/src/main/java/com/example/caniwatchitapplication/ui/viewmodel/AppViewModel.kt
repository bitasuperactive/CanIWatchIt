package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.watchmode.StreamingSource
import com.example.caniwatchitapplication.data.model.watchmode.TitleDetailsResponse
import com.example.caniwatchitapplication.data.repository.GithubRepository
import com.example.caniwatchitapplication.data.repository.PantryRepository
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.util.Constants.Companion.WATCHMODE_BACKUP_API_KEY
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Proporciona los datos requeridos por la aplicación.
 *
 * @param watchmodeRepo Repositorio de Watchmode
 * @param githubRepo Repositorio de la aplicación
 * @param pantryRepo Repositorio de Pantry
 */
class AppViewModel(
    private val watchmodeRepo: WatchmodeRepository,
    private val githubRepo: GithubRepository,
    private val pantryRepo: PantryRepository
) : ViewModel()
{
    /**
     * Lista de todas las claves api disponibles para Watchmode.
     */
    private val _watchmodeApiKeys = mutableListOf<String>()

    /**
     * Recupera una clave api aleatoria para Watchmode. Si no hay claves disponibles, devuelve
     * la clave api de respaldo.
     */
    private val watchmodeApiKeyDeferred: Deferred<String> = viewModelScope.async {

        if (_watchmodeApiKeys.isEmpty()) {
            fetchWatchmodeApiKeys()
        }

        _watchmodeApiKeys.randomOrNull() ?: WATCHMODE_BACKUP_API_KEY
    }

    private val _availableStreamingSources = MutableLiveData<Resource<List<StreamingSource>>>()

    private val _searchedTitles = MutableLiveData<Resource<List<TitleDetailsResponse>>>()

    /**
     * Plataformas de streaming disponibles para ser suscritas por el usuario.
     */
    val availableStreamingSources: LiveData<Resource<List<StreamingSource>>> = _availableStreamingSources

    /**
     * Plataformas de streaming suscritas por el usuario.
     */
    val subscribedStreamingSources = watchmodeRepo.getAllSubscribedStreamingSources()

    /**
     * Detalles de los títulos resultantes de la búsqueda del usuario.
     */
    val searchedTitles: LiveData<Resource<List<TitleDetailsResponse>>> = _searchedTitles

    init
    {
        getAvailableStreamingSources()
    }

    /**
     * Recupera las claves api para Watchmode disponibles en la base de datos remota y la filtra
     * en base al número de peticiones restantes.
     *
     * @see com.example.caniwatchitapplication.data.repository.PantryRepository.getAllApiKeys
     * @see com.example.caniwatchitapplication.data.repository.WatchmodeRepository.filterApiKeys
     */
    private suspend fun fetchWatchmodeApiKeys() = withContext(Dispatchers.IO) {

        try {
            val allApiKeysResource = pantryRepo.getAllApiKeys()

            if (allApiKeysResource is Resource.Success) {
                allApiKeysResource.data?.let { allKeys ->
                    allKeys.watchmode?.let { watchmodeKeys ->
                        val watchmodeFilteredKeys = watchmodeRepo.filterApiKeys(watchmodeKeys)
                        _watchmodeApiKeys.clear()
                        _watchmodeApiKeys.addAll(watchmodeFilteredKeys)
                    }
                }
            }

        } catch (t: Throwable) {
            if (t is IOException) {
                // Ignore, no need to manage
                return@withContext
            }
            throw t
        }
    }

    /**
     * @see GithubRepository.fetchAppLatestRelease
     */
    suspend fun fetchAppLatestRelease() = githubRepo.fetchAppLatestRelease()

    /**
     * @see WatchmodeRepository.searchForTitles
     */
    fun searchForTitles(searchValue: String) = viewModelScope.launch {
        
        _searchedTitles.postValue(Resource.Loading())

        try {
            val resource = watchmodeRepo.searchForTitles(
                watchmodeApiKeyDeferred.await(),
                searchValue
            )
            _searchedTitles.postValue(resource)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchedTitles.postValue(Resource.Error("Network failure"))
                else -> _searchedTitles.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }

    /**
     * @see WatchmodeRepository.upsertSubscribedStreamingSource
     */
    fun upsertSubscribedStreamingSource(streamingSource: StreamingSource) = viewModelScope.launch {

        watchmodeRepo.upsertSubscribedStreamingSource(streamingSource)
    }

    /**
     * @see WatchmodeRepository.deleteSubscribedStreamingSource
     */
    fun deleteSubscribedStreamingSource(streamingSource: StreamingSource) = viewModelScope.launch {
        
        watchmodeRepo.deleteSubscribedStreamingSource(streamingSource)
    }

    /**
     * @see WatchmodeRepository.getAllStreamingSources
     */
    private fun getAvailableStreamingSources() = viewModelScope.launch {

        _availableStreamingSources.postValue(Resource.Loading())

        try {
            val resource = watchmodeRepo.getAllStreamingSources(watchmodeApiKeyDeferred.await())
            _availableStreamingSources.postValue(resource)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _availableStreamingSources.postValue(Resource.Error("Network failure"))
                else -> _availableStreamingSources.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }
}