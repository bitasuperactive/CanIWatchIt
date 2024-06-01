package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Proporciona los datos requeridos por la aplicación.
 *
 * Loguea las peticiones restantes para la api y recupera el origen correspondiente las plataformas
 * de streaming disponibles a ser suscritas por el usuario.
 *
 * @param repository Repositorio de la aplicación
 *
 * @see AppRepository
 */
class AppViewModel(
    private val repository: AppRepository
) : ViewModel()
{
    private val _availableStreamingSources = MutableLiveData<Resource<List<StreamingSource>>>()
    private val _searchedTitles = MutableLiveData<Resource<List<TitleDetailsResponse>>>()

    /**
     * Plataformas de streaming disponibles para ser suscritas por el usuario.
     */
    val availableStreamingSources: LiveData<Resource<List<StreamingSource>>> = _availableStreamingSources

    /**
     * Detalles de los títulos resultantes de la búsqueda del usuario.
     */
    val searchedTitles: LiveData<Resource<List<TitleDetailsResponse>>> = _searchedTitles

    init
    {
        logApiQuota()
        getAvailableStreamingSources()
    }

    /**
     * @see AppRepository.searchForTitles
     */
    fun searchForTitles(searchValue: String) = viewModelScope.launch {
        
        _searchedTitles.postValue(Resource.Loading())
        
        try
        {
            val resource = repository.searchForTitles(searchValue)
            _searchedTitles.postValue(resource)
        } catch (t: Throwable)
        {
            when (t)
            {
                is IOException -> _searchedTitles.postValue(Resource.Error("Network failure"))
                else -> _searchedTitles.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }

    /**
     * @see AppRepository.getAllSubscribedStreamingSources
     */
    fun getAllSubscribedStreamingSources() = repository.getAllSubscribedStreamingSources()

    /**
     * @see AppRepository.upsertSubscribedStreamingSource
     */
    fun upsertSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = viewModelScope.launch {

        repository.upsertSubscribedStreamingSource(streamingSource)
    }

    /**
     * @see AppRepository.deleteSubscribedStreamingSource
     */
    fun deleteSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = viewModelScope.launch {
        
        repository.deleteSubscribedStreamingSource(streamingSource)
    }

    /**
     * @see AppRepository.getApiQuota
     */
    private fun logApiQuota() = viewModelScope.launch {

        repository.logApiQuota()
    }

    /**
     * @see AppRepository.getAllStreamingSources
     */
    private fun getAvailableStreamingSources() = viewModelScope.launch {

        _availableStreamingSources.postValue(Resource.Loading())

        try
        {
            val resource = repository.getAllStreamingSources()
            _availableStreamingSources.postValue(resource)
        } catch (t: Throwable)
        {
            when (t)
            {
                is IOException -> _availableStreamingSources.postValue(Resource.Error("Network failure"))
                else -> _availableStreamingSources.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }
}