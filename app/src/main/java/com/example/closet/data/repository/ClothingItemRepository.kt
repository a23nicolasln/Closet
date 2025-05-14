package com.example.closet.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.closet.data.dao.ClothingItemDao
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.relations.ClothingItemWithType
import com.google.firebase.database.FirebaseDatabase


class ClothingItemRepository (private val clothingItemDao: ClothingItemDao) {

    val clothingItemsWithType: LiveData<List<ClothingItemWithType>> =
        clothingItemDao.getClothingItemsWithType()

    suspend fun insertClothingItem(clothingItem: ClothingItem): Long {
        val id = clothingItemDao.insert(clothingItem)

        // Create a copy with the new generated ID
        val itemWithId = clothingItem.copy(clothingItemId = id)

        // Upload to Firebase
        FirebaseDatabase.getInstance()
            .getReference("clothingItems")
            .child(id.toString())
            .setValue(itemWithId)

        return id
    }


    fun getAllClothingItems(): LiveData<List<ClothingItem>> {
        return clothingItemDao.getAll()
    }

    suspend fun delete(clothingItem: ClothingItem) {
        clothingItemDao.delete(clothingItem)
    }

    suspend fun deleteById(clothingItemId: Long) {
        clothingItemDao.delete(clothingItemDao.getById(clothingItemId)!!)
    }

    suspend fun deleteAll() {
        clothingItemDao.deleteAll()
    }

    suspend fun update(clothingItem: ClothingItem) {
        FirebaseDatabase.getInstance()
            .getReference("clothingItems")
            .child(clothingItem.clothingItemId.toString())
            .setValue(clothingItem)

        clothingItemDao.update(clothingItem)
    }

    fun getClothingItemsByTypeId(type: Long): LiveData<List<ClothingItem>> {
        return clothingItemDao.getClothingItemsByType(type)
    }

    suspend fun getById(clothingItemId: Long): ClothingItem? {
        return clothingItemDao.getById(clothingItemId)
    }

    suspend fun getFilteredClothingItems(
        typeIds: List<Long>,
        attributeIds: List<Long>,
        colorIds: List<Long>
    ): List<ClothingItem> {
        val results = mutableListOf<List<ClothingItem>>()

        if (typeIds.isNotEmpty()) {
            val byType = typeIds.flatMap { clothingItemDao.getClothingItemsByTypeId(it) }
            results.add(byType)
        }

        if (attributeIds.isNotEmpty()) {
            val byAttribute = attributeIds.flatMap { clothingItemDao.getClothingItemsByAttributeId(it) }
            results.add(byAttribute)
        }

        if (colorIds.isNotEmpty()) {
            val byColor = colorIds.flatMap { clothingItemDao.getClothingItemsByColorId(it) }
            results.add(byColor)
        }

        return if (results.isEmpty()) {
            clothingItemDao.getAll().value ?: emptyList()
        } else {
            results.reduce { acc, list -> acc.intersect(list).toList() } // AND logic
        }
    }





}