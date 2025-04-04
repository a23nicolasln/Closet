package com.example.closet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.launch

class ClothingViewViewModel (private val repository: ClothingItemRepository) : ViewModel() {

    fun getById(id: Long): ClothingItem {
        var clothingItem = ClothingItem(0, "", "", "", "", "")
        viewModelScope.launch {
            clothingItem = repository.getById(id) ?: ClothingItem(0, "", "", "", "", "")
        }
        return clothingItem
    }

    fun delete(clothingItem: ClothingItem) {
        viewModelScope.launch {
            repository.delete(clothingItem)
        }
    }

}