package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color
import com.example.closet.data.relations.ClothingItemWithColors
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.TypeRepository
import kotlinx.coroutines.launch

class ClothingAddViewModel(
    private val clothingItemRepository: ClothingItemRepository,
    private val typeRepository: TypeRepository,
    private val colorRepository: ColorRepository,
    private val attributeRepository: AttributeRepository
) : ViewModel() {
    private val _currentItem = MutableLiveData<ClothingItem?>()
    val currentItem: MutableLiveData<ClothingItem?> get() = _currentItem

    fun getClothingItemById(id: Long) {
        viewModelScope.launch {
            val item = clothingItemRepository.getById(id)
            _currentItem.postValue(item)
        }
    }

    fun insertClothingItem(clothingItem: ClothingItem) {
        viewModelScope.launch {
            clothingItemRepository.insert(clothingItem)
        }
    }

    fun updateClothingItem(clothingItem: ClothingItem) {
        viewModelScope.launch {
            clothingItemRepository.update(clothingItem)
        }
    }

    fun deleteClothingItem(clothingItem: ClothingItem) {
        viewModelScope.launch {
            clothingItemRepository.delete(clothingItem)
        }
    }

    fun getAllClothingItems() = clothingItemRepository.getAllClothingItems()

    fun getClothingItemsByTypeId(typeId: Long) = clothingItemRepository.getClothingItemsByTypeId(typeId)

    fun getAllTypes() = typeRepository.getAllTypes()

    fun getTypeById(id: Long) = typeRepository.getTypeById(id)

    suspend fun getClothingItemColors(clothingItemId: Long): List<Color> {
        return colorRepository.getClothingItemColors(clothingItemId)
    }

    suspend fun getClothingItemAttributes(clothingItemId: Long): List<Attribute> {
        return attributeRepository.getClothingItemAttributes(clothingItemId)
    }

    suspend fun getAllColors(): List<Color> {
        return colorRepository.getAllColors()
    }




}

