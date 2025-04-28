package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
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

class FiltersViewModel(
    clothingItemRepository: ClothingItemRepository,
    typeRepository: TypeRepository,
    attributeRepository: AttributeRepository,
    colorRepository: ColorRepository,
    outfitRepository: OutfitRepository
) : ViewModel() {

    val clothingItems: LiveData<List<ClothingItem>> = clothingItemRepository.getAllClothingItems()
    val outfits: LiveData<List<Outfit>> = outfitRepository.getAllOutfits()
    val types: LiveData<List<Type>> = typeRepository.getAllTypes()
    val attributes: LiveData<List<Attribute>> = attributeRepository.getAllAttributes()
    val colors: LiveData<List<Color>> = colorRepository.getAllColors()
}
