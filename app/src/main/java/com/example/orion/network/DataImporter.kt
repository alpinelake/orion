package com.example.orion.network

import android.net.Uri
import com.example.orion.data.Item

interface DataImporter {
    suspend fun import(uri: Uri): List<Item>
}
