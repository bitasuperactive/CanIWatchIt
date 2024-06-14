package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.watchmode.StreamingSource
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.util.Constants.Companion.CONVERSION_FAILURE_MESSAGE
import com.example.caniwatchitapplication.util.Constants.Companion.NETWORK_FAILURE_MESSAGE
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Proporciona los datos requeridos por el fragmento de plataformas de streaming.
 *
 * @param appViewModel ViewModel principal.
 *
 * @see com.example.caniwatchitapplication.ui.view.fragments.StreamingSourcesFragment
 */
class StreamingSourcesViewModel(
    private val appViewModel: AppViewModel,
    stateHandle: SavedStateHandle
) : ViewModel()
{
    private val _availableStreamingSources = MutableLiveData<Resource<List<StreamingSource>>>()
    private val _informationVisibility = stateHandle.getLiveData<Int>("InformationVisibility")

    /**
     * Plataformas de streaming disponibles para ser suscritas por el usuario.
     */
    val availableStreamingSources: LiveData<Resource<List<StreamingSource>>> = _availableStreamingSources

    /**
     * Plataformas de streaming suscritas por el usuario.
     */
    val subscribedStreamingSources = appViewModel.watchmodeRepo.getAllSubscribedStreamingSources()

    /**
     * Visibilidad del TextView informativo.
     */
    val informationVisibility: LiveData<Int> = _informationVisibility

    init
    {
        getAvailableStreamingSources()
    }

    /**
     * @see WatchmodeRepository.upsertSubscribedStreamingSource
     */
    fun upsertSubscribedStreamingSource(streamingSource: StreamingSource) = viewModelScope.launch {

        appViewModel.watchmodeRepo.upsertSubscribedStreamingSource(streamingSource)
    }

    /**
     * @see WatchmodeRepository.deleteSubscribedStreamingSource
     */
    fun deleteSubscribedStreamingSource(streamingSource: StreamingSource) = viewModelScope.launch {

        appViewModel.watchmodeRepo.deleteSubscribedStreamingSource(streamingSource)
    }

    /**
     * Establece la visibilidad del TextView informativo.
     *
     * @param visibility Debe ser uno de los siguientes: View.GONE, View.VISIBLE, View.INVISIBLE
     */
    fun setInformationVisibility(visibility: Int) = _informationVisibility.postValue(visibility)

    /**
     * @see WatchmodeRepository.getAllStreamingSources
     */
    private fun getAvailableStreamingSources() = viewModelScope.launch {

        _availableStreamingSources.postValue(Resource.Loading())

        try {
            val resource = appViewModel.watchmodeRepo.getAllStreamingSources(
                appViewModel.watchmodeApiKeyDeferred.await()
            )
            _availableStreamingSources.postValue(resource)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _availableStreamingSources.postValue(
                    Resource.Error(NETWORK_FAILURE_MESSAGE)
                )
                else -> _availableStreamingSources.postValue(
                    Resource.Error(CONVERSION_FAILURE_MESSAGE)
                )
            }
        }
    }
}