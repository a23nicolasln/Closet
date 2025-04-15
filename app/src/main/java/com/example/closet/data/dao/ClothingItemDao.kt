package com.example.closet.data.dao

import android.adservices.adid.AdId
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.ClothingItemWithType
import kotlinx.coroutines.flow.Flow


@Dao
interface ClothingItemDao {

    @Query("SELECT * FROM ClothingItem")
    fun getAll(): LiveData<List<ClothingItem>>

    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE clothingItemId == :clothingItemId")
    suspend fun getItemWithTypeById(clothingItemId: Long): ClothingItemWithType?


    @Insert
    suspend fun insert(clothingItem: ClothingItem): Long

    @Delete
    suspend fun delete(clothingItem: ClothingItem)

    @Query("DELETE FROM ClothingItem")
    suspend fun deleteAll()

    @Update
    suspend fun update(clothingItem: ClothingItem)

    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE typeOwnerId == :typeId")
    fun getClothingItemsByTypeId( typeId: Long ): LiveData<List<ClothingItem>>

    @Transaction
    @Query("SELECT * FROM ClothingItem")
    fun getClothingItemsWithType(): LiveData<List<ClothingItemWithType>>


    @Query("SELECT * FROM ClothingItem WHERE clothingItemId == :clothingItemId")
    suspend fun getById(clothingItemId: Long): ClothingItem?
}