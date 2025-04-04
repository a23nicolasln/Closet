package com.example.closet.data.dao

import androidx.room.*
import com.example.closet.data.model.Outfit


@Dao
interface OutfitDao{

    @Query("SELECT * FROM Outfit")
    suspend fun getAll(): List<Outfit>

    @Insert
    suspend fun insert(outfit: Outfit): Long

    @Delete
    fun delete(outfit: Outfit)

    @Query("DELETE FROM Outfit")
    fun deleteAll()

    @Update
    fun update(outfit: Outfit)

    @Query("SELECT * FROM Outfit WHERE outfitId == :outfitId")
    fun getById(outfitId: Long): Outfit?

}