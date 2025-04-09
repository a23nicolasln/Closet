package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow


@Dao
interface ClothingItemDao{

    @Query("SELECT * FROM ClothingItem")
    fun getAll(): LiveData<List<ClothingItem>>

    @Insert
    suspend fun insert(clothingItem: ClothingItem): Long

    @Delete
    suspend fun delete(clothingItem: ClothingItem)

    @Query("DELETE FROM ClothingItem")
    suspend fun deleteAll()

    @Update
    suspend fun update(clothingItem: ClothingItem)

    @Query("SELECT * FROM ClothingItem WHERE type == :type")
    fun getByType(type: String): LiveData<List<ClothingItem>>?

    @Query("SELECT * FROM ClothingItem WHERE clothingItemId == :clothingItemId")
    suspend fun getById(clothingItemId: Long): ClothingItem?
}