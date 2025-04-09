package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OutfitAddClothingSelectorViewModel  (private val repository: ClothingItemRepository) : ViewModel() {


    fun getByType(type: String): LiveData<List<ClothingItem>>? {
        return repository.getByType(type)
    }

}