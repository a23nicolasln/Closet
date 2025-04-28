package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.ColorDao
import com.example.closet.data.model.Color
import com.example.closet.data.relations.ClothingItemColorCrossRef
import com.example.closet.data.relations.ClothingItemWithColors
import com.example.closet.data.relations.ColorWithClothingItems
import com.example.closet.data.relations.ColorWithOutfits
import com.example.closet.data.relations.OutfitColorCrossRef

class ColorRepository(
    private val colorDao: ColorDao
) {

    suspend fun insertColor(color: Color): Long =
        colorDao.insertColor(color)

    fun getAllColors(): LiveData<List<Color>>  =
        colorDao.getAllColors()

    suspend fun getColorWithClothingItems(colorId: Long): ColorWithClothingItems =
        colorDao.getColorWithClothingItems(colorId)

    suspend fun getColorWithOutfits(colorId: Long): ColorWithOutfits =
        colorDao.getColorWithOutfits(colorId)

    fun getClothingItemWithColors(clothingItemId: Long): LiveData<List<Color>> =
        colorDao.getClothingItemWithColors(clothingItemId)

    suspend fun addColorToClothingItem(clothingItemId: Long, colorId: Long) {
        colorDao.addColorToClothingItem(clothingItemId, colorId)
    }

    suspend fun deleteColorFromClothingItem(clothingItemId: Long, colorId: Long) {
        val ref = ClothingItemColorCrossRef(clothingItemId, colorId)
        colorDao.deleteClothingColorRef(ref)
    }

    fun getOutfitColors(outfitId: Long): LiveData<List<Color>> =
        colorDao.getOutfitColors(outfitId)

    suspend fun addColorToOutfit(outfitId: Long, colorId: Long) {
        val ref = OutfitColorCrossRef(outfitId, colorId)
        colorDao.insertOutfitColorRef(ref)
    }

    suspend fun deleteColorFromOutfit(outfitId: Long, colorId: Long) {
        val ref = OutfitColorCrossRef(outfitId, colorId)
        colorDao.deleteOutfitColorRef(ref)
    }
}
