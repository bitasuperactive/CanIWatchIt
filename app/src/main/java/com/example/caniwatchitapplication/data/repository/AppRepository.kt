package com.example.caniwatchitapplication.data.repository

import com.example.caniwatchitapplication.data.api.RetrofitProvider

class AppRepository
{
    suspend fun getAllSubscriptionServices() =
        RetrofitProvider.api.getAllSubscriptionServices()
    
    suspend fun searchForTitles(searchValue: String) =
        RetrofitProvider.api.searchForTitles(searchValue)
    
    suspend fun getTitleDetails(titleId: Int) =
        RetrofitProvider.api.getTitleDetails(titleId.toString())
}