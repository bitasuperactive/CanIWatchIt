package com.example.caniwatchitapplication.util

import com.example.caniwatchitapplication.data.model.Service
import com.example.caniwatchitapplication.data.model.TitleSource
import com.example.caniwatchitapplication.util.Constants.Companion.SERVICE_REGIONS
import com.example.caniwatchitapplication.util.Constants.Companion.SERVICE_TYPES

class Transformers
{
    companion object
    {
        fun getServicesFromSources(services: List<Service>, sources: List<TitleSource>): List<Service>
        {
            return services.filter { service ->
                sources.any { source ->
                    source.id == service.id
                            && source.type == SERVICE_TYPES
                            && source.region == SERVICE_REGIONS
                }
            }
        }
    }
}