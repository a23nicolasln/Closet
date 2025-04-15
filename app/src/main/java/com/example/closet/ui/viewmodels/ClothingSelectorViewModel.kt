package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClothingSelectorViewModel (private val repository: ClothingItemRepository) : ViewModel() {


    fun getClothingItemsByTypeId(type: Long): LiveData<List<ClothingItem>> {
        return repository.getClothingItemsByTypeId(type)
    }

}