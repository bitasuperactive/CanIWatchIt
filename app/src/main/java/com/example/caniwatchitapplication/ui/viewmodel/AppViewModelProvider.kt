package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.caniwatchitapplication.data.repository.AppRepository

class AppViewModelProvider(
    private val repository: AppRepository
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {

            return AppViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}