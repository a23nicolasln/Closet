package com.example.closet.repository

import com.example.closet.data.dao.ColorDao
import com.example.closet.data.model.Color
import com.example.closet.data.relations.ColorWithClothingItems
import com.example.closet.data.relations.ColorWithOutfits

class ColorRepository(
    private val colorDao: ColorDao
) {

    suspend fun insertColor(color: Color): Long =
        colorDao.insertColor(color)

    suspend fun getAllColors(): List<Color> =
        colorDao.getAllColors()

    suspend fun getColorWithClothingItems(colorId: Long): ColorWithClothingItems =
        colorDao.getColorWithClothingItems(colorId)

    suspend fun getColorWithOutfits(colorId: Long): ColorWithOutfits =
        colorDao.getColorWithOutfits(colorId)
}
