package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.ClothingItemDao
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.relations.ClothingItemWithType


class ClothingItemRepository (private val clothingItemDao: ClothingItemDao) {

    val clothingItemsWithType: LiveData<List<ClothingItemWithType>> =
        clothingItemDao.getClothingItemsWithType()

    suspend fun insert(clothingItem: ClothingItem) {
        clothingItemDao.insert(clothingItem)
    }

    fun getAllClothingItems(): LiveData<List<ClothingItem>> {
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

    fun getClothingItemsByTypeId(type: Long): LiveData<List<ClothingItem>> {
        return clothingItemDao.getClothingItemsByTypeId(type)
    }

    suspend fun getById(clothingItemId: Long): ClothingItem? {
        return clothingItemDao.getById(clothingItemId)
    }
}