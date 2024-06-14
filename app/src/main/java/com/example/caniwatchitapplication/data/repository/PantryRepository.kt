package com.example.caniwatchitapplication.data.repository

import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.model.pantry.ApiKeysResponse
import com.example.caniwatchitapplication.util.Resource

/**
 * Recupera los datos de la api Pantry requeridos por el ViewModel.
 */
class PantryRepository
{
    /**
     * @see com.example.caniwatchitapplication.data.api.PantryApi.getAllApiKeys
     */
    suspend fun getAllApiKeys(): Resource<ApiKeysResponse>
    {
        val response = RetrofitProvider.pantryApi.getAllApiKeys()

        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }
}
