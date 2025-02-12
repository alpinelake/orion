package com.example.orion.network

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode
import com.example.orion.data.Item
import com.example.orion.data.ItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DataImporterImpl(private val context: Context) : DataImporter {

    override suspend fun import(uri: Uri): List<Item> {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                var items: List<Item> = emptyList()
                if (inputStream != null) {
                    // create a temp file and copy the data so room can use it
                    val file = File.createTempFile("import", null, context.cacheDir)
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    val database = Room.databaseBuilder(
                        context,
                        ItemDatabase::class.java,
                        file.absolutePath
                    )
                        .setJournalMode(JournalMode.TRUNCATE)
                        .build()

                    items = database.itemDao().getItemsFlat()

                    file.delete()
                }
                items
            }
        }
    }
}
