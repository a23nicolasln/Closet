package com.example.closet.data.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.TypeDao
import com.example.closet.data.model.Type

class TypeRepository(private val typeDao: TypeDao) {


    suspend fun insert(type: Type) {
        typeDao.insertType(type)
    }

    fun getAllTypes(): LiveData<List<Type>> {
        return typeDao.getAllTypes()
    }

    fun getTypeById(typeId: Long): LiveData<Type> {
        return typeDao.getTypeById(typeId)
    }

    suspend fun delete(type: Type) {
        typeDao.deleteType(type)
    }
}
