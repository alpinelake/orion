package com.example.orion.data

import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getItems(): Flow<List<Item>>
    fun getOwners(): Flow<List<Owner>>
    suspend fun insert(item: Item)
    suspend fun update(item: Item)
    suspend fun delete(item: Item)
    suspend fun insert(owner: Owner): Long
    suspend fun get(id: Long): Owner
    suspend fun insertReturnResult(owner: Owner) : Owner
    suspend fun update(owner: Owner)
    suspend fun delete(owner: Owner)
}
