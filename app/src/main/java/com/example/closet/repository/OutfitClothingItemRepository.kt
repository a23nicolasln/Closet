package com.example.closet.repository

import com.example.closet.data.dao.OutfitClothingItemDao
import com.example.closet.data.model.OutfitClothingItemCrossRef


class OutfitClothingItemRepository(private val outfitClothingItemDao: OutfitClothingItemDao) {
    suspend fun insert(outfitClothingItem: OutfitClothingItemCrossRef) {
        outfitClothingItemDao.insert(outfitClothingItem)
    }

    suspend fun getOutfitWithClothingItems(outfitId: Long){
        outfitClothingItemDao.getOutfitWithClothingItems(outfitId)
    }

    suspend fun getClothingItemWithOutfits(clothingItemId: Long) {
        outfitClothingItemDao.getClothingItemWithOutfits(clothingItemId)
    }

}