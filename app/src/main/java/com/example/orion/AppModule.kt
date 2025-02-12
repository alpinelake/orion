package com.example.orion

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode
import com.example.orion.data.DataRepository
import com.example.orion.data.DataRepositoryImpl
import com.example.orion.data.ItemDao
import com.example.orion.data.ItemDatabase
import com.example.orion.data.SettingsDataStoreManager
import com.example.orion.network.DataImporter
import com.example.orion.network.DataImporterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    const val DATABASE_NAME = "orion"
    const val DATABASE_FILENAME = "$DATABASE_NAME.db"

    @Singleton
    @Provides
    fun provideDataRepository(itemDao: ItemDao): DataRepository {
        return DataRepositoryImpl(itemDao)
    }

    @Singleton
    @Provides
    fun provideItemDatabase(@ApplicationContext context: Context): ItemDatabase {
        return Room.databaseBuilder(context, ItemDatabase::class.java, DATABASE_FILENAME)
            .setJournalMode(JournalMode.TRUNCATE)
            .build()
    }

    @Singleton
    @Provides
    fun provideDataImporter(@ApplicationContext context: Context): DataImporter {
        return DataImporterImpl(context)
    }

    @Singleton
    @Provides
    fun provideSettingsDataStoreManager(@ApplicationContext context: Context): SettingsDataStoreManager {
        return SettingsDataStoreManager(context)
    }

    @Singleton
    @Provides
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }

                }
                level = LogLevel.ALL
            }
            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("HTTP status:", "${response.status.value}")
                }
            }
        }
    }

    @Singleton
    @Provides
    fun provideItemDao(db: ItemDatabase): ItemDao {
        return db.itemDao()
    }
}
