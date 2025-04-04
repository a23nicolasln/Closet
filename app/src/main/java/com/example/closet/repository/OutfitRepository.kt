package com.example.closet.repository

import com.example.closet.data.dao.OutfitDao
import com.example.closet.data.model.Outfit


class OutfitRepository(private val outfitDao: OutfitDao) {
    suspend fun insert(outfit: Outfit) {
        outfitDao.insert(outfit)
    }

    suspend fun getAll(): List<Outfit> {
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