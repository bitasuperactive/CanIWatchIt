package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.watchmode.TitleDetailsResponse
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.util.Constants.Companion.CONVERSION_FAILURE_MESSAGE
import com.example.caniwatchitapplication.util.Constants.Companion.NETWORK_FAILURE_MESSAGE
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Proporciona los datos requeridos por el fragmento de búsqueda de títulos.
 *
 * @param appViewModel ViewModel principal.
 *
 * @see com.example.caniwatchitapplication.ui.view.fragments.SearchFragment
 */
class SearchViewModel(
    private val appViewModel: AppViewModel,
    stateHandle: SavedStateHandle
) : ViewModel()
{
    private val _query = stateHandle.getLiveData<String>("Query")
    private val _hintVisibility = stateHandle.getLiveData<Int>("HintVisibility")
    private val _searchedTitles = MutableLiveData<Resource<List<TitleDetailsResponse>>>()

    /**
     * Entrada del usuario para la búsqueda.
     */
    val query: LiveData<String> = _query

    /**
     * Visibilidad de la pista de búsqueda.
     */
    val hintVisibility: LiveData<Int> = _hintVisibility

    /**
     * Detalles de los títulos resultantes de la búsqueda.
     */
    val searchedTitles: LiveData<Resource<List<TitleDetailsResponse>>> = _searchedTitles

    /**
     * @see WatchmodeRepository.isQueryLimitReached
     */
    fun isQueryLimitReached(): Deferred<Boolean> = viewModelScope.async {
        appViewModel.watchmodeRepo.isQueryLimitReached()
    }

    /**
     * Establece el valor de búsqueda si este es diferente al almacenado.
     */
    fun setQuery(query: String)
    {
        if (query != _query.value) {
            _query.postValue(query)
        }
    }

    /**
     * @see WatchmodeRepository.addQueryCount
     */
    fun addQueryCount() = viewModelScope.launch {
        appViewModel.watchmodeRepo.addQueryCount()
    }

    /**
     * Establece la visibilidad de la pista de búsqueda.
     */
    fun setHintVisibility(visibility: Int)
    {
        _hintVisibility.postValue(visibility)
    }

    /**
     * @see WatchmodeRepository.searchForTitles
     */
    fun searchForTitles(searchValue: String) = viewModelScope.launch {

        _searchedTitles.postValue(Resource.Loading())

        try {
            val resource = appViewModel.watchmodeRepo.searchForTitles(
                appViewModel.watchmodeApiKeyDeferred.await(),
                searchValue
            )
            _searchedTitles.postValue(resource)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchedTitles.postValue(
                    Resource.Error(NETWORK_FAILURE_MESSAGE)
                )
                else -> _searchedTitles.postValue(
                    Resource.Error(CONVERSION_FAILURE_MESSAGE)
                )
            }
        }
    }
}