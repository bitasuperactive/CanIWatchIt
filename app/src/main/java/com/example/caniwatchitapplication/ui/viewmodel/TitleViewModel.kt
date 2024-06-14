package com.example.caniwatchitapplication.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Proporciona los datos requeridos por el fragmento de los títulos.
 *
 * @see com.example.caniwatchitapplication.ui.view.fragments.TitleFragment
 */
class TitleViewModel(stateHandle: SavedStateHandle) : ViewModel()
{
    private val _dialogAlreadyShown = stateHandle.getLiveData("DialogAlreadyShown", false)
    private val _javaScriptEnabled = stateHandle.getLiveData("JavaScriptEnabled", false)

    /**
     * El díalogo de JavaScript ya ha sido mostrado.
     */
    val dialogAlreadyShown = _dialogAlreadyShown

    /**
     * JavaScript está habilitado.
     */
    val javaScriptEnabled = _javaScriptEnabled

    /**
     * El diálogo de JavaScript ha sido mostrado.
     */
    fun dialogShown() { _dialogAlreadyShown.postValue(true) }

    /**
     * Habilitar JavaScript.
     */
    fun enableJavaScript() { _javaScriptEnabled.postValue(true) }
}