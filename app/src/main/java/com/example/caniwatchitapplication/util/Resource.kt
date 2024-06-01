package com.example.caniwatchitapplication.util

/**
 * Clase genérica que declara los estados de las peticiones a la api y a la base de datos.
 *
 * - Loading: La petición está en proceso.
 * - Error: La petición ha resultado errónea.
 * - Success: La petición ha resultado satisfactoria.
 *
 * @param data Cuerpo de la respuesta
 * @param message Mensaje del error producido
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
