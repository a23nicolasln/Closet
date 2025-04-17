package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.Outfit
import com.example.closet.data.relations.OutfitColorCrossRef
import com.example.closet.data.relations.OutfitWithColors
import kotlinx.coroutines.flow.Flow


@Dao
interface OutfitDao{

    @Query("SELECT * FROM Outfit")
    fun getAll(): LiveData<List<Outfit>>

    @Insert
    suspend fun insert(outfit: Outfit): Long

    @Delete
    suspend fun delete(outfit: Outfit)

    @Query("DELETE FROM Outfit")
    suspend fun deleteAll()

    @Update
    suspend fun update(outfit: Outfit)

    @Query("SELECT * FROM Outfit WHERE outfitId == :outfitId")
    suspend fun getById(outfitId: Long): Outfit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: Outfit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitColorCrossRef(crossRef: OutfitColorCrossRef)

    @Transaction
    @Query("SELECT * FROM Outfit WHERE outfitId = :id")
    suspend fun getOutfitWithColors(id: Long): OutfitWithColors

    @Transaction
    @Query("""
        SELECT * FROM Outfit
        WHERE outfitId IN (
            SELECT outfitId FROM OutfitColorCrossRef WHERE colorId = :colorId
        )
    """)
    suspend fun getOutfitsByColor(colorId: Long): List<Outfit>

}