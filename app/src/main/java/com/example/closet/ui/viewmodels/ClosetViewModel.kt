package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.TypeRepository
import kotlinx.coroutines.launch

class ClosetViewModel(
    private val clothingItemRepository: ClothingItemRepository,
    private val typeRepository: TypeRepository
) : ViewModel() {

    val allTypes: LiveData<List<Type>> = typeRepository.getAllTypes()

    fun insertType(type: Type) {
        viewModelScope.launch {
            typeRepository.insert(type)
        }
    }

    fun getClothingItemsByTypeId(typeId: Long): LiveData<List<ClothingItem>> {
        return clothingItemRepository.getClothingItemsByTypeId(typeId)
    }
}
