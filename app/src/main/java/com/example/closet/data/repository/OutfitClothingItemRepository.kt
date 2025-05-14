package com.example.closet.data.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.OutfitClothingItemDao
import com.example.closet.data.relations.ClothingItemWithOutfits
import com.example.closet.data.relations.OutfitClothingItemCrossRef
import com.example.closet.data.relations.OutfitWithClothingItems


class OutfitClothingItemRepository(private val outfitClothingItemDao: OutfitClothingItemDao) {
    suspend fun insert(outfitClothingItem: OutfitClothingItemCrossRef) {
        outfitClothingItemDao.insert(outfitClothingItem)
    }

    fun getOutfitWithClothingItems(outfitId: Long): LiveData<List<OutfitWithClothingItems>> {
        return outfitClothingItemDao.getOutfitWithClothingItems(outfitId)
    }

    fun getClothingItemWithOutfits(clothingItemId: Long): LiveData<List<ClothingItemWithOutfits>> {
        return outfitClothingItemDao.getClothingItemWithOutfits(clothingItemId)
    }

    suspend fun delete(outfitClothingItem: OutfitClothingItemCrossRef) {
        outfitClothingItemDao.delete(outfitClothingItem)
    }

}