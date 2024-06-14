package com.example.caniwatchitapplication.ui.viewmodel.providers

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.ui.viewmodel.SearchViewModel
import com.example.caniwatchitapplication.ui.viewmodel.StreamingSourcesViewModel
import com.example.caniwatchitapplication.ui.viewmodel.TitleViewModel

/**
 * Proveedor del ViewModel principal para los ViewModels de los fragmentos.
 *
 * @see SearchViewModel
 * @see StreamingSourcesViewModel
 * @see TitleViewModel
 */
class FragmentViewModelProvider(
    private val appViewModel: AppViewModel
) : AbstractSavedStateViewModelFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T
    {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(appViewModel, handle) as T
        }

        if (modelClass.isAssignableFrom(StreamingSourcesViewModel::class.java)) {
            return StreamingSourcesViewModel(appViewModel, handle) as T
        }

        if (modelClass.isAssignableFrom(TitleViewModel::class.java)) {
            return TitleViewModel(handle) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}