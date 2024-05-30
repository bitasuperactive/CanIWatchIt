package com.example.caniwatchitapplication.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.caniwatchitapplication.data.model.StreamingSource

@Dao
interface SubscribedStreamingSourcesDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streamingSource: StreamingSource): Long
    
    @Query("SELECT * FROM subscribed_services")
    fun getAllSubscribedStreamingSources(): LiveData<List<StreamingSource>>
    
    @Delete
    suspend fun delete(streamingSource: StreamingSource)
}