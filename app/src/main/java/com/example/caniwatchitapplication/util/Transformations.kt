package com.example.caniwatchitapplication.util

import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleStreamingSource
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_REGIONS
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_TYPES

/**
 * Clase que contiene métodos de transformación y filtrado de objetos.
 */
class Transformations
{
    companion object
    {
        /**
         * Filtra la lista de plataformas de streaming para obtener aquellas que cumplan con unos
         * criterios de coincidencia prestablecidos.
         *
         * _Dado que la api no nos devuelve la misma información para las plataformas en que
         * un título está disponible que para todas las plataformas soportadas, necesitamos
         * realizar esta conversión y, así obtener por ejemplo, el url a los logos de las
         * plataformas._
         *
         * @param titleSources Lista de plataformas de streaming en las que un título
         * específico se encuentra disponible.
         *
         * @return Lista de plataformas de streaming que cumplen con los criterios de coincidencia
         * para los títulos proporcionados.
         *
         * @see STREAMING_SOURCE_TYPES
         * @see STREAMING_SOURCE_REGIONS
         */
        fun List<StreamingSource>.filterByTitleSources(
            titleSources: List<TitleStreamingSource>
        ): List<StreamingSource>
        {
            return this.filter { source ->
                titleSources.any { titleSource ->
                    titleSource.id == source.id
                            // Criterios de coincidencia
                            && titleSource.type == STREAMING_SOURCE_TYPES
                            && titleSource.region == STREAMING_SOURCE_REGIONS
                }
            }
        }

        /**
         * Transforma el modelo, correspondiente a una plataforma de streaming, en una entidad de
         * plataforma de streaming suscrita por el usuario, para su inserción en la base de datos
         * de Room.
         */
        fun StreamingSource.toEntity() : SubscribedStreamingSourceEntity
        {
            return SubscribedStreamingSourceEntity(
                this.id,
                this.name,
                this.logoUrl
            )
        }

        /**
         * Transforma el listado de entidades, correspondientes a las plataformas de streaming
         * suscritas por el usuario, en un listado de entidades equivalente.
         */
        fun List<SubscribedStreamingSourceEntity>.toModelList(): List<StreamingSource>
        {
            return this.map {
                StreamingSource(it.id, it.name, it.logoUrl)
            }
        }
    }
}