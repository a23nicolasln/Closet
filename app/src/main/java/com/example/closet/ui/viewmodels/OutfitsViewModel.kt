package com.example.closet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Outfit
import com.example.closet.repository.OutfitRepository
import kotlinx.coroutines.launch

class OutfitsViewModel (private val repository: OutfitRepository) : ViewModel() {
    var outfits: List<Outfit> = emptyList()

    fun getAll(): List<Outfit> {
        viewModelScope.launch {
            outfits = repository.getAll()
        }
        return outfits
    }

}