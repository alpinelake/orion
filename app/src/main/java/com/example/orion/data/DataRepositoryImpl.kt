package com.example.orion.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
) : DataRepository {

    override fun getItems(): Flow<List<Item>> = itemDao.getItems()
    override fun getOwners(): Flow<List<Owner>> = itemDao.getOwners()
    override suspend fun insert(item: Item): Long = itemDao.insert(item)
    override suspend fun update(item: Item) = itemDao.update(item)
    override suspend fun delete(item: Item) = itemDao.delete(item)
    override suspend fun insert(owner: Owner): Long = itemDao.insert(owner)
    override suspend fun get(id: Long): Owner = itemDao.get(id)
    override suspend fun insertReturnResult(owner: Owner) : Owner = itemDao.insertReturnResult(owner)
    override suspend fun update(owner: Owner) = itemDao.update(owner)
    override suspend fun delete(owner: Owner) = itemDao.delete(owner)
}
