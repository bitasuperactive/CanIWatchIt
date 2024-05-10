package com.example.caniwatchitapplication.data.repository

import androidx.lifecycle.LiveData
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.db.AppDatabase
import com.example.caniwatchitapplication.data.model.Service

class AppRepository(
    private val db: AppDatabase
)
{
    suspend fun getAllSubscriptionServices() =
        RetrofitProvider.api.getAllSubscriptionServices()
    
    suspend fun searchForTitles(searchValue: String) =
        RetrofitProvider.api.searchForTitles(searchValue)
    
    suspend fun getTitleDetails(titleId: Int) =
        RetrofitProvider.api.getTitleDetails(titleId.toString())
    
    suspend fun upsertSubscribedService(service: Service): Long =
        db.getServiceDao().upsert(service)
    
    fun getAllSubscribedServices(): LiveData<List<Service>> =
        db.getServiceDao().getAllSubscribedServices()
    
    suspend fun deleteSubscribedService(service: Service) =
        db.getServiceDao().delete(service)
}