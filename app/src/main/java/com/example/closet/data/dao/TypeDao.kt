package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.closet.data.model.Type

@Dao
interface TypeDao {

    @Query("SELECT * FROM Type")
    fun getAllTypes(): LiveData<List<Type>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertType(type: Type)

    @Delete
    suspend fun deleteType(type: Type)

    @Query("SELECT * FROM Type WHERE typeId = :typeId")
    fun getTypeById(typeId: Long): LiveData<Type>

}
