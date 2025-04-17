package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.relations.ClothingItemColorCrossRef
import com.example.closet.data.relations.ClothingItemWithColors
import com.example.closet.data.relations.ClothingItemWithType


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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingItem(item: ClothingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingItemColorCrossRef(crossRef: ClothingItemColorCrossRef)

    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE clothingItemId = :id")
    suspend fun getClothingItemWithColors(id: Long): ClothingItemWithColors

    @Transaction
    @Query("""
        SELECT * FROM ClothingItem
        WHERE clothingItemId IN (
            SELECT clothingItemId FROM ClothingItemColorCrossRef WHERE colorId = :colorId
        )
    """)
    suspend fun getClothingItemsByColor(colorId: Long): List<ClothingItem>
}