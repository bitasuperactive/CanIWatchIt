package com.example.caniwatchitapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao
import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity
import com.example.caniwatchitapplication.util.Constants

/**
 * Crea una instancia única de la base de datos de Room.
 */
@Database(
    entities = [SubscribedStreamingSourceEntity::class],
    version = 1,
    exportSchema = false
)
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
                Constants.APP_DATABASE_NAME
            ).build()
        }
    }
}