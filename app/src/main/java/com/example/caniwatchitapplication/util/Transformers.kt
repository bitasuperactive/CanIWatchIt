package com.example.caniwatchitapplication.util

import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleStreamingSource
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_REGIONS
import com.example.caniwatchitapplication.util.Constants.Companion.STREAMING_SOURCE_TYPES

class Transformers
{
    companion object
    {
        fun getStreamingSourcesFromTitles(
            streamingSources: List<StreamingSource>,
            titleStreamingSources: List<TitleStreamingSource>
        ): List<StreamingSource>
        {
            return streamingSources.filter { source ->
                titleStreamingSources.any { titleSource ->
                    titleSource.id == source.id
                            && titleSource.type == STREAMING_SOURCE_TYPES
                            && titleSource.region == STREAMING_SOURCE_REGIONS
                }
            }
        }
    }
}