package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.OutfitDao
import com.example.closet.data.model.Outfit
import kotlinx.coroutines.flow.Flow


class OutfitRepository(private val outfitDao: OutfitDao) {
    suspend fun insert(outfit: Outfit) {
        outfitDao.insert(outfit)
    }

    fun getAll(): LiveData<List<Outfit>> {
        return outfitDao.getAll()
    }

    suspend fun delete(outfit: Outfit) {
        outfitDao.delete(outfit)
    }

    suspend fun deleteAll() {
        outfitDao.deleteAll()
    }

    suspend fun update(outfit: Outfit) {
        outfitDao.update(outfit)
    }

    suspend fun getById(outfitId: Long): Outfit? {
        return outfitDao.getById(outfitId)
    }
}