package com.example.closet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.launch

class OutfitAddClothingSelectorViewModel  (private val repository: ClothingItemRepository) : ViewModel() {
    var clothingItems: List<ClothingItem> = emptyList()

    fun getByType(type: String): List<ClothingItem> {
        viewModelScope.launch {
            clothingItems = repository.getByType(type)?: emptyList()
        }
        return clothingItems
    }

}