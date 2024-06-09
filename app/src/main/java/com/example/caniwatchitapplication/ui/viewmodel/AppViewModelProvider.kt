package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.caniwatchitapplication.data.repository.GithubRepository
import com.example.caniwatchitapplication.data.repository.PantryRepository
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository

class AppViewModelProvider(
    private val watchmodeRepo: WatchmodeRepository,
    private val githubRepo: GithubRepository,
    private val pantryRepo: PantryRepository
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {

            return AppViewModel(watchmodeRepo, githubRepo, pantryRepo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}