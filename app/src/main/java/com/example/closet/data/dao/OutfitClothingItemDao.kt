package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.relations.ClothingItemWithOutfits
import com.example.closet.data.relations.OutfitClothingItemCrossRef
import com.example.closet.data.relations.OutfitWithClothingItems


@Dao
interface OutfitClothingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: OutfitClothingItemCrossRef)

    @Transaction
    @Query("SELECT * FROM Outfit WHERE outfitId == :outfitId")
    fun getOutfitWithClothingItems(outfitId: Long): LiveData<List<OutfitWithClothingItems>>

    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE clothingItemId == :clothingItemId ")
    fun getClothingItemWithOutfits(clothingItemId: Long): LiveData<List<ClothingItemWithOutfits>>

    @Delete
    suspend fun delete(crossRef: OutfitClothingItemCrossRef)
}