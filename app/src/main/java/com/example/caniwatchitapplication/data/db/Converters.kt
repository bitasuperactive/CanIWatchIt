package com.example.caniwatchitapplication.data.db

import androidx.room.TypeConverter
import com.example.caniwatchitapplication.data.model.Service

class Converters
{
    @TypeConverter
    fun fromService(service: Service): String
    {
        return service.logo100pxUrl
    }
    
    @TypeConverter
    fun toService(logoUrl: String): Service
    {
        // TODO - Not fully implemented.
        return Service(0, "", logoUrl)
    }
}