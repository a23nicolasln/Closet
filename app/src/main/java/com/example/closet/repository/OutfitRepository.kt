package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.OutfitDao
import com.example.closet.data.model.Outfit


class OutfitRepository(private val outfitDao: OutfitDao) {
    suspend fun insert(outfit: Outfit): Long {
        return outfitDao.insert(outfit)
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

    fun getAllOutfits(): LiveData<List<Outfit>> {
        return outfitDao.getAllOutfits()
    }

    suspend fun getFilteredOutfits(
        selectedAttributes: List<Long>,
        selectedColors: List<Long>
    ): List<Outfit> {
        val results = mutableListOf<List<Outfit>>()

        if (selectedAttributes.isNotEmpty()) {
            val byAttribute = selectedAttributes.flatMap { outfitDao.getOutfitsByAttributeId(it) }
            results.add(byAttribute)
        }

        if (selectedColors.isNotEmpty()) {
            val byColor = selectedColors.flatMap { outfitDao.getOutfitsByColorId(it) }
            results.add(byColor)
        }

        return if (results.isNotEmpty()) {
            results.reduce { acc, list -> acc.intersect(list).toList() }
        } else {
            emptyList()
        }
    }
}