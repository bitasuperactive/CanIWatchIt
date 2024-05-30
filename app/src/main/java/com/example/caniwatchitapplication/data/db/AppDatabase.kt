package com.example.caniwatchitapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.caniwatchitapplication.data.model.StreamingSource

@Database(
    entities = [StreamingSource::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun getSubscribedStreamingSourcesDao(): SubscribedStreamingSourcesDao
    
    companion object
    {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()
        
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            
            instance ?: createDatabase(context).also { instance = it }
        }
        
        private fun createDatabase(context: Context): AppDatabase
        {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "caniwatchit_db.db"
            ).build()
        }
    }
}