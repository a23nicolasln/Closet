package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit
import com.example.closet.data.model.OutfitClothingItemCrossRef
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
import kotlinx.coroutines.launch

class OutfitCreationViewModel(
    private val outfitRepo: OutfitRepository,
    private val clothingRepo: ClothingItemRepository,
    private val joinRepo: OutfitClothingItemRepository
) : ViewModel() {

    // Private mutable backing properties
    private val _currentOutfit = MutableLiveData<Outfit?>()
    private val _selectedItems = MutableLiveData<List<ClothingItem>>(emptyList())

    // Public immutable LiveData
    val currentOutfit: LiveData<Outfit?> = _currentOutfit
    val selectedItems: LiveData<List<ClothingItem>> = _selectedItems

    fun updateOutfit(outfit: Outfit) {
        _currentOutfit.value = outfit
    }

    fun addClothingItem(item: ClothingItem) {
        _selectedItems.value = _selectedItems.value.orEmpty() + item
    }

    fun getClothingItemsByType(type: String): LiveData<List<ClothingItem>>? {
        return clothingRepo.getByType(type)
    }

    suspend fun createNewOutfit() {
        val newOutfit = Outfit(name = "New Outfit", imageUrl = "")
        val outfitId = outfitRepo.insert(newOutfit)
        _currentOutfit.value = newOutfit.copy(outfitId = outfitId)
    }

    fun clearOutfitData() {
        _currentOutfit.value = null
        _selectedItems.value = emptyList()
    }

    fun saveOutfit() {
        viewModelScope.launch {
            val outfit = _currentOutfit.value ?: return@launch
            val items = _selectedItems.value ?: emptyList()

            // Save relations
            items.forEach { item ->
                joinRepo.insert(OutfitClothingItemCrossRef(
                    outfitId = outfit.outfitId,
                    clothingItemId = item.clothingItemId
                ))
            }
            // Save outfit
            outfitRepo.update(outfit)

            // Clear data after saving
            clearOutfitData()
        }
    }
}
