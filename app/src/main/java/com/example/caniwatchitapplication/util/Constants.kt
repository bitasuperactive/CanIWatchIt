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
         * Clave api de respaldo para Watchmode en caso de que la base de datos remota no esté
         * disponible o no devuelva ninguna clave para Watchmode.
         */
        const val WATCHMODE_BACKUP_API_KEY = "DTTAVpb3QzVjtZdADZJ1YiVo3MzIzHWWJfWFukvo"

        /**
         * Límite de la cantidad de títulos detallados a recuperar de la api.
         *
         * _Al trabajar con 1000 peticiones mensuales que proporciona la versión grauita de la api,
         * es obligado limitar la funcionalidad de la aplicación._
         */
        const val MAX_TITLE_DETAILS_REQUESTS = 1

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
         * Tipos de plataforma de streaming a filtrar al buscar títulos.
         *
         * Tipos disponibles: [API Docs](https://api.watchmode.com/docs/#list-titles)
         */
        const val STREAMING_SOURCE_TYPES = "sub"

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
        const val DAYS_TO_UPDATE_AVAILABLE_STREAMING_SOURCES = 1
    }
}