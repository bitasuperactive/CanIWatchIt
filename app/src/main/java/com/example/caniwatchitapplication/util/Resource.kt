package com.example.caniwatchitapplication.util

/**
 * Clase genérica que declara los estados de las peticiones a la api.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
)
{
    class Loading<T> : Resource<T>()
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Success<T>(data: T) : Resource<T>(data)
}
