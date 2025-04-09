package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Outfit
import com.example.closet.repository.OutfitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OutfitsViewModel (private val repository: OutfitRepository) : ViewModel() {
    var outfits: LiveData<List<Outfit>> = repository.getAll()

    fun getAll(): LiveData<List<Outfit>> {
        viewModelScope.launch {
            outfits = repository.getAll()
        }
        return outfits
    }

}