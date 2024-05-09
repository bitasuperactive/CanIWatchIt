package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.model.ServicesResponse
import com.example.caniwatchitapplication.data.model.Title
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.data.model.TitlesResponse
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.util.Constants.Companion.MAX_SIMULTANEOUS_API_REQUESTS
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AppViewModel(
    private val repository: AppRepository
) : ViewModel()
{
    private val _availableServices = MutableLiveData<Resource<ServicesResponse>>()
    private val _searchedTitles = MutableLiveData<Resource<List<TitleDetailsResponse>>>()
    
    val availableServices: LiveData<Resource<ServicesResponse>> = _availableServices
    val searchedTitles: LiveData<Resource<List<TitleDetailsResponse>>> = _searchedTitles
    
    init
    {
        getAvailableServices()
    }
    
    fun searchForTitles(searchValue: String) = viewModelScope.launch {
        
        _searchedTitles.postValue(Resource.Loading())
        
        try
        {
            val response = repository.searchForTitles(searchValue)
            _searchedTitles.postValue(handleTitlesResponse(response))
        } catch (t: Throwable)
        {
            when (t) {
                is IOException -> _searchedTitles.postValue(Resource.Error("Network failure"))
                else -> _searchedTitles.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }
    
    private fun getAvailableServices() = viewModelScope.launch {
        
        _availableServices.postValue(Resource.Loading())
        
        try
        {
            val response = repository.getAllSubscriptionServices()
            _availableServices.postValue(handleAvailableServicesResponse(response))
        } catch (t: Throwable)
        {
            when (t) {
                is IOException -> _availableServices.postValue(Resource.Error("Network failure"))
                else -> _availableServices.postValue(Resource.Error("Json to Kotlin conversion failure"))
            }
        }
    }
    
    private fun handleAvailableServicesResponse(response: Response<ServicesResponse>): Resource<ServicesResponse>
    {
        if (response.isSuccessful) {
            
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        
        return Resource.Error(response.message(), response.body())
    }
    
    private suspend fun handleTitlesResponse(response: Response<TitlesResponse>): Resource<List<TitleDetailsResponse>>
    {
        if (response.isSuccessful) {
            
            response.body()?.let {
                return getTitlesDetails(it.titles)
            }
        }
    
        return Resource.Error(response.message(), null)
    }
    
    private suspend fun getTitlesDetails(titles: List<Title>): Resource<List<TitleDetailsResponse>>
    {
        val result = ArrayList<TitleDetailsResponse>(titles.size)
        
        // Calls limited by the following constant.
        for (title in titles.take(MAX_SIMULTANEOUS_API_REQUESTS))
        {
            val response = repository.getTitleDetails(title.id)
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