package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caniwatchitapplication.data.repository.GithubRepository
import com.example.caniwatchitapplication.data.repository.PantryRepository
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.io.IOException

/**
 * ViewModel principal. Proporciona las instancias de los repositorios de la aplicación además de
 * los datos del repositorio en GitHub y las claves api de los Endpoints.
 *
 * @param watchmodeRepo Repositorio de Watchmode
 * @param githubRepo Repositorio de la aplicación
 * @param pantryRepo Repositorio de Pantry
 */
open class AppViewModel(
    val watchmodeRepo: WatchmodeRepository,
    val githubRepo: GithubRepository,
    val pantryRepo: PantryRepository
) : ViewModel()
{
    /**
     * Lista de todas las claves api disponibles para Watchmode.
     */
    private val _watchmodeApiKeys = mutableListOf<String>()

    /**
     * Proporciona una clave api aleatoria para Watchmode.
     *
     * Si no hay claves api disponibles, recibiremos el error 401 "Unauthorized" del Endpoint.
     *
     * @see fetchWatchmodeApiKeys
     */
    val watchmodeApiKeyDeferred: Deferred<String> = viewModelScope.async {

        if (_watchmodeApiKeys.isEmpty()) {
            _watchmodeApiKeys.addAll(fetchWatchmodeApiKeys())
        }

        _watchmodeApiKeys.randomOrNull() ?: ""
    }

    /**
     * @see GithubRepository.fetchAppLatestRelease
     */
    suspend fun fetchAppLatestRelease() = githubRepo.fetchAppLatestRelease()

    /**
     * Recupera las claves api para Watchmode de la base de datos remota.
     *
     * @return Lista de claves api para Watchmode que disponen del mínimo de peticiones restantes
     * requerido por día
     *
     * @see com.example.caniwatchitapplication.data.repository.PantryRepository.getAllApiKeys
     * @see com.example.caniwatchitapplication.data.repository.WatchmodeRepository.filterApiKeys
     */
    private suspend fun fetchWatchmodeApiKeys(): List<String>
    {
        var keyList = emptyList<String>()

        try {
            val allApiKeysResource = pantryRepo.getAllApiKeys()

            if (allApiKeysResource is Resource.Success) {
                allApiKeysResource.data?.let { allKeys ->
                    allKeys.watchmode?.let { watchmodeKeys ->
                        keyList = watchmodeKeys
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return watchmodeRepo.filterApiKeys(keyList)
    }
}