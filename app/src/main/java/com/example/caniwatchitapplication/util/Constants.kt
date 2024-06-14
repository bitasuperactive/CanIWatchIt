package com.example.caniwatchitapplication.util

class Constants
{
    companion object
    {
        /**
         * Enlace base a la api de Watchmode.
         */
        const val WATCHMODE_API_BASE_URL = "https://api.watchmode.com"

        /**
         * Enlace al repositorio de la aplicación en GitHub.
         */
        const val APP_REPOSITORY_BASE_URL = "https://github.com/bitasuperactive/CanIWatchIt/"

        /**
         * Enlace base a la api de Pantry.
         */
        const val PANTRY_API_BASE_URL = "https://getpantry.cloud"

        /**
         * Nombre de la base de datos de Room para esta aplicación.
         */
        const val APP_DATABASE_NAME = "caniwatchit_db.db"

        /**
         * Clave api gratuita de respaldo para Watchmode en caso de que la base de datos remota no esté
         * disponible o no devuelva ninguna clave para Watchmode.
         */
        const val WATCHMODE_BACKUP_API_KEY = "DTTAVpb3QzVjtZdADZJ1YiVo3MzIzHWWJfWFukvo"

        /**
         * Cantidad límite de búsquedas diarias.
         */
        const val QUERY_LIMIT_PER_DAY = 10  // Use -1 for unlimited queries

        /**
         * Límite de la cantidad de títulos detallados a recuperar de la api.
         *
         * _Al trabajar con claves api gratuitas, es obligado limitar la funcionalidad de la
         * aplicación._
         */
        const val MAX_TITLE_DETAILS_REQUESTS = 5

        /**
         * Calcula el número de peticiones de Watchmode requeridas para satisfacer el servicio
         * diario para cada usuario.
         *
         * Por ejemplo, si QUERY_LIMIT_PER_DAY = 10 y MAX_TITLE_DETAILS_REQUESTS = 5, la operación sería la
         * siguiente: 10 + (10 * 5) = 60 peticiones por día para cada usuario.
         *
         * _Se añaden 10 peticiones adicionales para contemplar búsquedas nulas._
         *
         * @see QUERY_LIMIT_PER_DAY
         * @see MAX_TITLE_DETAILS_REQUESTS
         */
        const val QUOTA_NEEDED_PER_DAY = QUERY_LIMIT_PER_DAY + (QUERY_LIMIT_PER_DAY * MAX_TITLE_DETAILS_REQUESTS) + 10

        /**
         * Idioma de los detalles del título, como la sinopsis, a buscar.
         *
         * Idiomas disponibles: [API Docs](https://api.watchmode.com/docs/#title)
         */
        const val TITLE_LANGUAGE = "ES"

        /**
         * Tipos de contenido a filtrar al buscar títulos.
         *
         * Tipos disponibles: [API Docs](https://api.watchmode.com/docs/#list-titles)
         */
        const val TITLE_TYPES = "tv,movie"

        /**
         * Regiones a filtrar al buscar títulos.
         *
         * _Cada región adicional consumirá 1 credito más de la api._
         *
         * Regiones disponibles: [API Docs](https://api.watchmode.com/docs/#list-titles)
         */
        const val STREAMING_SOURCE_REGIONS = "ES"

        /**
         * Tipos de plataforma de streaming a filtrar al buscar títulos. Una cadena vacía equivale
         * a todos los tipos disponibles.
         *
         * Tipos disponibles: [API Docs](https://api.watchmode.com/docs/#list-titles)
         */
        const val STREAMING_SOURCE_TYPES = "sub,free,tve"

        /**
         * Tamaño mínimo que pueden tomar los logos de las plataformas de streaming.
         *
         * _Se utiliza en el "displayer" para mostrar al usuario las plataformas a las que está
         * suscrito o aquellas para las que un título se encuentra disponible._
         */
        const val MIN_STREAMING_SOURCE_LOGO_PX_SIZE = 130

        /**
         * Tamaño máximo que pueden tomar los logos de las plataformas de streaming.
         *
         * _Se utiliza en el fragmento de plataformas de streaming disponibles para suscribirse._
         */
        const val MAX_STREAMING_SOURCE_LOGO_PX_SIZE = 200

        /**
         * Tiempo de espera para el input del usuario. Evita llamadas innecesarias a la api.
         */
        const val SEARCH_FOR_TITLES_DELAY = 500L

        /**
         * Define el número de días tras las cuales se deben actualizar las plataformas de
         * streaming disponibles.
         */
        const val DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES = 7

        /**
         * Mensaje por defecto para los errores de red.
         */
        const val NETWORK_FAILURE_MESSAGE = "Network failure"

        /**
         * Mensaje por defecto para los errores de conversión de Json a Kotlin.
         */
        const val CONVERSION_FAILURE_MESSAGE = "Json to Kotlin conversion failure"

        /**
         * Mensaje por defecto para los errores de Watchmode referentes a la validez de la clave api.
         */
        const val INVALID_API_KEY_MESSAGE = "Las claves api han sido agotadas para el mes en curso."
    }
}