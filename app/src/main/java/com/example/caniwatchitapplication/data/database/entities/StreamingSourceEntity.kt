package com.example.caniwatchitapplication.data.database.entities

/**
 * Interfaz que define la estructura de las entidades referentes a las plataformas de streaming.
 */
interface StreamingSourceEntity {
    /**
     * Identificador único en la api.
     */
    val id: Int

    /**
     * Nombre comercial.
     */
    val name: String

    /**
     * Url del logo (100x100 píxeles).
     */
    val logoUrl: String
}