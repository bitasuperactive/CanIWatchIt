package com.example.caniwatchitapplication.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.caniwatchitapplication.data.model.Service

@Dao
interface SubscribedServiceDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(service: Service): Long
    
    @Query("SELECT * FROM subscribed_services")
    fun getAllSubscribedServices(): LiveData<List<Service>>
    
    @Delete
    suspend fun delete(service: Service)
}