package com.example.caniwatchitapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.caniwatchitapplication.data.database.converters.Converters
import com.example.caniwatchitapplication.data.database.dao.AvailableStreamingSourcesDao
import com.example.caniwatchitapplication.data.database.dao.SubscribedStreamingSourcesDao
import com.example.caniwatchitapplication.data.database.entities.AvailableStreamingSourceEntity
import com.example.caniwatchitapplication.data.database.entities.SubscribedStreamingSourceEntity
import com.example.caniwatchitapplication.util.Constants

/**
 * Base de datos Room orientada únicamente a almacenar plataformas de streaming; aquellas
 * suscritas por el usuario y aquellas disponibles para ser suscritas.
 *
 * _No se toman en cuenta las versiones de la base de datos, los cambios son destructivos._
 *
 * @see SubscribedStreamingSourceEntity
 * @see AvailableStreamingSourceEntity
 */
@Database(
    entities = [SubscribedStreamingSourceEntity::class, AvailableStreamingSourceEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun getAvailableStreamingSourcesDao(): AvailableStreamingSourcesDao
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
            ).fallbackToDestructiveMigration().build()
        }
    }
}