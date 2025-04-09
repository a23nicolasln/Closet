package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.Outfit
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

}