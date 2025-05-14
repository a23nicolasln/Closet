package com.example.closet.ui.viewmodels

import androidx.lifecycle.*
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.TypeRepository
import kotlinx.coroutines.launch

class ClosetViewModel(
    private val clothingItemRepository: ClothingItemRepository,
    private val typeRepository: TypeRepository
) : ViewModel() {

    val allTypes: LiveData<List<Type>> = typeRepository.getAllTypes()

    val allClothingItems: LiveData<List<ClothingItem>> = clothingItemRepository.getAllClothingItems()

    val clothingItemsByType: LiveData<Map<Long, List<ClothingItem>>> = allClothingItems.map { items ->
        items.groupBy { it.typeOwnerId }
    }

    fun insertType(type: Type) {
        viewModelScope.launch {
            typeRepository.insert(type)
        }
    }
}

