package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.StreamingSourcesResponse
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class AppViewModel(
    private val repository: AppRepository
) : ViewModel()
{
    private val _availableStreamingSources = MutableLiveData<Resource<StreamingSourcesResponse>>()
    private val _searchedTitles = MutableLiveData<Resource<List<TitleDetailsResponse>>>()

    val availableStreamingSources: LiveData<Resource<StreamingSourcesResponse>> = _availableStreamingSources
    val searchedTitles: LiveData<Resource<List<TitleDetailsResponse>>> = _searchedTitles
    
    init
    {
        logQuota()
        getAvailableStreamingSources()
    }
    
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

    fun getAllSubscribedStreamingSources() = repository.getAllSubscribedStreamingSources()
    
    fun upsertSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = viewModelScope.launch {

        repository.upsertSubscribedStreamingSource(streamingSource)
    }
    
    fun deleteSubscribedStreamingSource(
        streamingSource: StreamingSource
    ) = viewModelScope.launch {
        
        repository.deleteSubscribedStreamingSource(streamingSource)
    }

    private fun logQuota() = viewModelScope.launch {

        repository.logQuota()
    }

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