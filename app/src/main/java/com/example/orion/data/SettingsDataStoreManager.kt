package com.example.orion.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.orion.ItemSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Keys = object {
    val HIDE_ARCHIVED = booleanPreferencesKey("hide_archived")
    val DEFAULT_SORT = stringPreferencesKey("default_sort")
}

class SettingsDataStoreManager(context: Context) {
    private val Context.dataStore by preferencesDataStore("settings")
    private val dataStore = context.dataStore

    val hideArchived: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.HIDE_ARCHIVED] ?: false
    }

    val defaultSort: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.DEFAULT_SORT] ?: ItemSort.Date.name
    }

    suspend fun setHideArchived(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HIDE_ARCHIVED] = value
        }
    }

    suspend fun setDefaultSort(value: String) {
        dataStore.edit { preferences ->
            preferences[Keys.DEFAULT_SORT] = value
        }
    }
}
