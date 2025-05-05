package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color
import com.example.closet.data.model.Outfit
import com.example.closet.data.model.Type
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.repository.TypeRepository
import kotlinx.coroutines.launch

class FiltersViewModel(
    private val clothingItemRepository: ClothingItemRepository,
    private val typeRepository: TypeRepository,
    private val attributeRepository: AttributeRepository,
    private val colorRepository: ColorRepository,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    val types: LiveData<List<Type>> = typeRepository.getAllTypes()
    val attributes: LiveData<List<Attribute>> = attributeRepository.getAllAttributes()
    val colors: LiveData<List<Color>> = colorRepository.getAllColors()

    private var selectedTypes = mutableListOf<Type>()
    private var selectedAttributes = mutableListOf<Attribute>()
    private var selectedColors = mutableListOf<Color>()

    private val _filteredClothingItems = MutableLiveData<List<ClothingItem>>()
    val filteredClothingItems: LiveData<List<ClothingItem>> get() = _filteredClothingItems

    private val _filteredOutfits = MutableLiveData<List<Outfit>>()
    val filteredOutfits: LiveData<List<Outfit>> get() = _filteredOutfits


    // SELECTED FUNCTIONS

    fun selectType(type: Type) {
        if (selectedTypes.contains(type)) {
            selectedTypes.remove(type)
        } else {
            selectedTypes.add(type)
        }
    }

    fun selectAttribute(attribute: Attribute) {
        if (selectedAttributes.contains(attribute)) {
            selectedAttributes.remove(attribute)
        } else {
            selectedAttributes.add(attribute)
        }
    }

    fun selectColor(color: Color) {
        if (selectedColors.contains(color)) {
            selectedColors.remove(color)
        } else {
            selectedColors.add(color)
        }
    }

    fun getSelectedTypes(): List<Type> {
        return selectedTypes
    }

    fun getSelectedAttributes(): List<Attribute> {
        return selectedAttributes
    }

    fun getSelectedColors(): List<Color> {
        return selectedColors
    }

    fun clearSelection() {
        selectedTypes.clear()
        selectedAttributes.clear()
        selectedColors.clear()
    }

    fun clearTypeSelection() {
        selectedTypes.clear()
    }


    // DAO FUNCTIONS

    fun insertType(type: Type) {
        viewModelScope.launch {
            typeRepository.insert(type)
        }
    }

    fun insertAttribute(attribute: Attribute) {
        viewModelScope.launch {
            attributeRepository.insertAttribute(attribute)
        }
    }

    fun insertColor(color: Color) {
        viewModelScope.launch {
            colorRepository.insertColor(color)
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            for (type in selectedTypes) {
                typeRepository.delete(type)
            }
            for (attribute in selectedAttributes) {
                attributeRepository.delete(attribute)
            }
            for (color in selectedColors) {
                colorRepository.delete(color)
            }
        }
    }

    fun applyClothingItemFilters() {
        viewModelScope.launch {
            val result = clothingItemRepository.getFilteredClothingItems(
                selectedTypes.map { it.typeId },
                selectedAttributes.map { it.attributeId },
                selectedColors.map { it.colorId }
            )
            _filteredClothingItems.postValue(result)
        }
    }

    fun applyOutfitFilters() {
        viewModelScope.launch {
            val result = outfitRepository.getFilteredOutfits(
                selectedAttributes.map { it.attributeId },
                selectedColors.map { it.colorId }
            )
            _filteredOutfits.postValue(result)
        }
    }

}
