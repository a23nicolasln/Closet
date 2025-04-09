package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Outfit
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.OutfitRepository
import kotlinx.coroutines.launch

class OutfitAddViewModel(private val repository: OutfitRepository) : ViewModel() {

    private val _outfit = MutableLiveData<Outfit>()
    val outfit: LiveData<Outfit> get() = _outfit

    fun setOutfit(outfit: Outfit) {
        _outfit.value = outfit
    }

    fun insertOutfit(outfit: Outfit) {
        viewModelScope.launch {
            repository.insert(outfit)
        }
    }

}
