package com.example.caniwatchitapplication.data.db

import androidx.room.TypeConverter
import com.example.caniwatchitapplication.data.model.StreamingSource

class Converters
{
    @TypeConverter
    fun fromStreamingSource(source: StreamingSource): String
    {
        return source.logo100pxUrl
    }
    
    @TypeConverter
    fun toStreamingSource(logoUrl: String): StreamingSource
    {
        // TODO - Not fully implemented.
        return StreamingSource(0, "", logoUrl)
    }
}