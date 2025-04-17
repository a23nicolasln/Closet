package com.example.closet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.closet.data.model.Color
import com.example.closet.data.relations.ClothingItemColorCrossRef
import com.example.closet.data.relations.ClothingItemWithColors
import com.example.closet.data.relations.ColorWithClothingItems
import com.example.closet.data.relations.ColorWithOutfits
import com.example.closet.data.relations.OutfitColorCrossRef
import com.example.closet.data.relations.OutfitWithColors

@Dao
interface ColorDao {

    // Color operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColor(color: Color): Long

    @Query("SELECT * FROM Color")
    suspend fun getAllColors(): List<Color>

    // Relations with ClothingItem
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingColorRef(ref: ClothingItemColorCrossRef)

    @Delete
    suspend fun deleteClothingColorRef(ref: ClothingItemColorCrossRef)

    @Transaction
    @Query("SELECT * FROM Color WHERE colorId = :colorId")
    suspend fun getColorWithClothingItems(colorId: Long): ColorWithClothingItems

    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE clothingItemId = :itemId")
    suspend fun getClothingItemWithColors(itemId: Long): ClothingItemWithColors

    // Relations with Outfit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitColorRef(ref: OutfitColorCrossRef)

    @Delete
    suspend fun deleteOutfitColorRef(ref: OutfitColorCrossRef)

    @Transaction
    @Query("SELECT * FROM Color WHERE colorId = :colorId")
    suspend fun getColorWithOutfits(colorId: Long): ColorWithOutfits

    @Transaction
    @Query("SELECT * FROM Outfit WHERE outfitId = :outfitId")
    suspend fun getOutfitWithColors(outfitId: Long): OutfitWithColors
}
