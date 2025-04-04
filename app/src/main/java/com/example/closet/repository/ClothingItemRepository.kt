package com.example.closet.repository

import com.example.closet.data.dao.ClothingItemDao
import com.example.closet.data.model.ClothingItem


class ClothingItemRepository (private val clothingItemDao: ClothingItemDao) {

    suspend fun insert(clothingItem: ClothingItem) {
        clothingItemDao.insert(clothingItem)
    }

    suspend fun getAll(): List<ClothingItem> {
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

    suspend fun getByType(type: String): List<ClothingItem>? {
        return clothingItemDao.getByType(type)
    }

    suspend fun getById(clothingItemId: Long): ClothingItem? {
        return clothingItemDao.getById(clothingItemId)
    }
}