package com.example.caniwatchitapplication.util

import com.example.caniwatchitapplication.data.database.entities.AvailableStreamingSourceEntity
import com.example.caniwatchitapplication.data.database.entities.StreamingSourceEntity
import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleStreamingSource
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_REGIONS
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_TYPES
import java.time.LocalDate

/**
 * Métodos de transformación y filtrado de objetos.
 */
class Transformations
{
    companion object
    {
        /**
         * Recrea la lista de plataformas añadiendo los enlaces correspondientes a sus logos y
         * la filtra en base al tipo de contenido y la región de comercialización.
         *
         * @param availableStreamingSources Plataformas de streaming de las que extraer
         * los enlaces a los logos
         *
         * @see addLogos
         * @see STREAMING_SOURCE_TYPES
         * @see STREAMING_SOURCE_REGIONS
         */
        fun List<TitleStreamingSource>.recreate(
            availableStreamingSources: List<StreamingSource>
        ): List<TitleStreamingSource>
        {
            val titleSourcesWithLogos = this.addLogos(availableStreamingSources)

            return titleSourcesWithLogos.filter { titleSource ->
                titleSource.type == STREAMING_SOURCE_TYPES
                        && titleSource.region == STREAMING_SOURCE_REGIONS
            }
        }

        /**
         * Añade los enlaces a los logos de las plataformas especificadas.
         *
         * _Dado que la api no nos devuelve la misma información para las plataformas en que
         * un título está disponible que para todas las plataformas soportadas, necesitamos
         * realizar esta transformación para recuperar los logos._
         *
         * @param availableStreamingSources Plataformas de streaming de las que extraer
         * los enlaces a los logos
         */
        private fun List<TitleStreamingSource>.addLogos(
            availableStreamingSources: List<StreamingSource>
        ): List<TitleStreamingSource>
        {
            return this.map { titleSource ->
                titleSource.copy(
                    logoUrl = availableStreamingSources.firstOrNull { source ->
                        source.id == titleSource.id
                    }?.logoUrl ?: ""
                )
            }
        }

        /**
         * Transforma el modelo, correspondiente a una plataforma de streaming, en una entidad de
         * plataforma de streaming suscrita por el usuario, para su inserción en la base de datos
         * de Room.
         */
        fun StreamingSource.toSubscribedEntity() : SubscribedStreamingSourceEntity
        {
            return SubscribedStreamingSourceEntity(
                this.id ?: 0,
                this.name ?: "",
                this.logoUrl ?: ""
            )
        }

        /**
         * Transforma el listado de entidades, representativas de plataformas de streaming, en sus
         * modelos equivalentes.
         */
        fun<T> List<T>.toModelList(): List<StreamingSource> where T : StreamingSourceEntity
        {
            return this.map {
                StreamingSource(it.id, it.name, it.logoUrl)
            }
        }

        /**
         * Transforma el listado de plataformas de streaming en sus entidades equivalentes
         * para las plataformas disponibles.
         */
        fun List<StreamingSource>.toAvailableEntityList(): List<AvailableStreamingSourceEntity>
        {
            return this.map {
                AvailableStreamingSourceEntity(
                    it.id ?: 0,
                    it.name ?: "",
                    it.logoUrl ?: "",
                    LocalDate.now()
                )
            }
        }
    }
}