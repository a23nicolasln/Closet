package com.example.closet.data.dao

import androidx.lifecycle.LiveData
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
    fun getAllColors(): LiveData<List<Color>>

    // Relations with ClothingItem
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingColorRef(ref: ClothingItemColorCrossRef)

    @Delete
    suspend fun deleteClothingColorRef(ref: ClothingItemColorCrossRef)

    @Transaction
    @Query("SELECT * FROM Color WHERE colorId = :colorId")
    suspend fun getColorWithClothingItems(colorId: Long): ColorWithClothingItems

    @Query("""
    SELECT c.* FROM Color c
    INNER JOIN ClothingItemColorCrossRef ref
    ON c.colorId = ref.colorId
    WHERE ref.clothingItemId = :id
""")
    fun getClothingItemWithColors(id: Long): LiveData<List<Color>>

    // Relations with Outfit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitColorRef(ref: OutfitColorCrossRef)

    @Delete
    suspend fun deleteOutfitColorRef(ref: OutfitColorCrossRef)

    @Transaction
    @Query("SELECT * FROM Color WHERE colorId = :colorId")
    suspend fun getColorWithOutfits(colorId: Long): ColorWithOutfits

    @Query("""
    SELECT c.* FROM Color c
    INNER JOIN ClothingItemColorCrossRef ref ON c.colorId = ref.colorId
    WHERE ref.clothingItemId = :id
""")
    suspend fun getColorsForClothingItem(id: Long): List<Color>

    suspend fun addColorToClothingItem(clothingItemId: Long, colorId: Long) {
        val ref = ClothingItemColorCrossRef(clothingItemId, colorId)
        insertClothingColorRef(ref)
    }

    @Query("""
    SELECT c.* FROM Color c
    INNER JOIN OutfitColorCrossRef ref 
    ON c.colorId = ref.colorId
    WHERE ref.outfitId = :outfitId
    """)
    fun getOutfitColors(outfitId: Long): LiveData<List<Color>>

    @Delete
    suspend fun delete(color: Color)

}
