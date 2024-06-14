package com.example.caniwatchitapplication.ui.viewmodel.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.caniwatchitapplication.data.repository.GithubRepository
import com.example.caniwatchitapplication.data.repository.PantryRepository
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel

/**
 * Proveedor de los repositorios de la aplicación para el ViewModel principal.
 *
 * @see AppViewModel
 */
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