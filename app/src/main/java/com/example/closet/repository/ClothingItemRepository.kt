package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.ClothingItemDao
import com.example.closet.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow


class ClothingItemRepository (private val clothingItemDao: ClothingItemDao) {

    suspend fun insert(clothingItem: ClothingItem) {
        clothingItemDao.insert(clothingItem)
    }

    fun getAll(): LiveData<List<ClothingItem>> {
        return clothingItemDao.getAll()
    }

    suspend fun delete(clothingItem: ClothingItem) {
        clothingItemDao.delete(clothingItem)
    }

    suspend fun deleteAll() {
        clothingItemDao.deleteAll()
    }

    suspend fun update(clothingItem: ClothingItem) {
        clothingItemDao.update(clothingItem)
    }

    fun getByType(type: String): LiveData<List<ClothingItem>>? {
        return clothingItemDao.getByType(type)
    }

    suspend fun getById(clothingItemId: Long): ClothingItem? {
        return clothingItemDao.getById(clothingItemId)
    }
}