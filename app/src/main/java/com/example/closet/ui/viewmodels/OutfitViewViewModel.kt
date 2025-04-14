package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit
import com.example.closet.data.model.OutfitWithClothingItems
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
import kotlinx.coroutines.launch

class OutfitViewViewModel (private val repository: OutfitRepository,private val joinedRepository: OutfitClothingItemRepository) : ViewModel() {

    fun getById(id: Long): Outfit {
        var outfit: Outfit? = null
        viewModelScope.launch {
            outfit = repository.getById(id)
        }
        return outfit ?: Outfit( name = "", imageUrl = "")
    }

    fun getClothingItemsByOutfitId(outfitId: Long): LiveData<List<OutfitWithClothingItems>> {
        return joinedRepository.getOutfitWithClothingItems(outfitId)
    }


    fun delete(outfit: Outfit) {
        viewModelScope.launch {
            repository.delete(outfit)
        }
    }

}