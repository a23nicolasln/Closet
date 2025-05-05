package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color
import com.example.closet.data.relations.ClothingItemAttributeCrossRef
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

    suspend fun insertClothingItem(clothingItem: ClothingItem): Long {

        return clothingItemRepository.insertClothingItem(clothingItem)
    }

    suspend fun updateClothingItem(clothingItem: ClothingItem) {
        clothingItemRepository.update(clothingItem)
    }

    suspend fun deleteClothingItemById(clothingItemId: Long) {
        clothingItemRepository.deleteById(clothingItemId)
    }

    fun getClothingItemColors(clothingItemId: Long): LiveData<List<Color>> {
        return colorRepository.getClothingItemWithColors(clothingItemId)
    }

    fun getClothingItemAttributes(clothingItemId: Long): LiveData<List<Attribute>> {
        return attributeRepository.getClothingItemAttributes(clothingItemId)
    }

    suspend fun addColorToClothingItem(clothingItemId: Long, colorId: Long) {
        colorRepository.addColorToClothingItem(clothingItemId, colorId)
    }

    fun getAllColors(): LiveData<List<Color>> {
        return colorRepository.getAllColors()
    }

    suspend fun deleteColorFromClothingItem(clothingItemId: Long, colorId: Long) {
        colorRepository.deleteColorFromClothingItem(clothingItemId, colorId)
    }

    fun getAllAttributes(): LiveData<List<Attribute>> {
        return attributeRepository.getAllAttributes()
    }

    suspend fun addAttributeToClothingItem(clothingItemId: Long, attributeId: Long) {
        attributeRepository.addAttributeToClothingItem(ClothingItemAttributeCrossRef(
            clothingItemId = clothingItemId,
            attributeId = attributeId
        ))
    }

    suspend fun deleteAttributeFromClothingItem(clothingItemId: Long, attributeId: Long) {
        attributeRepository.deleteAttributeFromClothingItem(ClothingItemAttributeCrossRef(
            clothingItemId = clothingItemId,
            attributeId = attributeId
        ))
    }

    fun clearCurrentItem() {
        _currentItem.value = null
    }

    fun createNewAttribute(name: String) {
        viewModelScope.launch {
            val newAttribute = Attribute(name = name)
            attributeRepository.insertAttribute(newAttribute)
        }
    }

    fun insertColor(color: Color) {
        viewModelScope.launch {
            colorRepository.insertColor(color)
        }
    }




}

