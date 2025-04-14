package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClothingViewViewModel(private val repository: ClothingItemRepository) : ViewModel() {
    private val _clothingItem = MutableLiveData<ClothingItem?>()
    val clothingItem: LiveData<ClothingItem?> = _clothingItem

    fun loadClothingItem(id: Long) {
        viewModelScope.launch {
            val item = repository.getById(id)
            _clothingItem.postValue(item)
        }
    }

    fun delete(item: ClothingItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

}
