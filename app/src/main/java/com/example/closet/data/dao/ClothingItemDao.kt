package com.example.closet.data.dao

import androidx.room.*
import com.example.closet.data.model.ClothingItem


@Dao
interface ClothingItemDao{

    @Query("SELECT * FROM ClothingItem")
    suspend fun getAll(): List<ClothingItem>

    @Insert
    suspend fun insert(clothingItem: ClothingItem): Long

    @Delete
    fun delete(clothingItem: ClothingItem)

    @Query("DELETE FROM ClothingItem")
    fun deleteAll()

    @Update
    fun update(clothingItem: ClothingItem)

    @Query("SELECT * FROM ClothingItem WHERE type == :type")
    fun getByType(type: String): List<ClothingItem>?

    @Query("SELECT * FROM ClothingItem WHERE clothingItemId == :clothingItemId")
    fun getById(clothingItemId: Long): ClothingItem?
}