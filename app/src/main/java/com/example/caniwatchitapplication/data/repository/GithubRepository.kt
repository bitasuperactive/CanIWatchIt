package com.example.caniwatchitapplication.data.repository

import com.example.caniwatchitapplication.data.api.RetrofitProvider
import com.example.caniwatchitapplication.data.model.github.AppVersionInfo
import com.example.caniwatchitapplication.util.Resource

class GithubRepository
{
    /**
     * @see com.example.caniwatchitapplication.data.api.GithubApi.getLatestRelease
     */
    suspend fun fetchAppLatestRelease():Resource<AppVersionInfo>
    {
        val response = RetrofitProvider.githubApi.getLatestRelease()

        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message(), response.body())
    }
}
