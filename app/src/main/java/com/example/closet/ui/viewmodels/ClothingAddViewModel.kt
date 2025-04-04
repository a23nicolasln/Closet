package com.example.closet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.*
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.launch

class ClothingAddViewModel (private val repository: ClothingItemRepository) : ViewModel() {

    fun insertClothingItem(clothingItem: ClothingItem) {
        viewModelScope.launch {
            repository.insert(clothingItem)
        }
    }

}