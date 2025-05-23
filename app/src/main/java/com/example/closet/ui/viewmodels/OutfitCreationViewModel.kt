package com.example.closet.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color
import com.example.closet.data.model.Outfit
import com.example.closet.data.relations.OutfitClothingItemCrossRef
import com.example.closet.data.relations.OutfitWithClothingItems
import com.example.closet.data.model.Type
import com.example.closet.data.relations.OutfitAttributeCrossRef
import com.example.closet.data.repository.AttributeRepository
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.ColorRepository
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.data.repository.TypeRepository
import kotlinx.coroutines.launch

class OutfitCreationViewModel(
    private val outfitRepo: OutfitRepository,
    private val clothingRepo: ClothingItemRepository,
    private val joinRepo: OutfitClothingItemRepository,
    private val typeRepo: TypeRepository,
    private val colorRepository: ColorRepository,
    private val attributeRepository: AttributeRepository
) : ViewModel() {

    // Private mutable backing properties
    private val _currentOutfit = MutableLiveData<Outfit?>()
    private val _selectedItems = MutableLiveData<List<ClothingItem>>(emptyList())

    // Public immutable LiveData
    val currentOutfit: LiveData<Outfit?> = _currentOutfit
    val selectedItems: LiveData<List<ClothingItem>> = _selectedItems
    val allTypes: LiveData<List<Type>> = typeRepo.getAllTypes()

    // New LiveData to hold clothing items grouped by type
    val allClothingItems: LiveData<List<ClothingItem>> = clothingRepo.getAllClothingItems()
    val clothingItemsByType: LiveData<Map<Long, List<ClothingItem>>> = allClothingItems.map { items ->
        items.groupBy { it.typeOwnerId }
    }


    // Load clothing items by type and update the LiveData
    fun loadClothingItemsByType() {
        viewModelScope.launch {
            val types = allTypes.value ?: return@launch
            val groupedItems = types.associate { type ->
                type.typeId to (clothingRepo.getClothingItemsByTypeId(type.typeId).value ?: emptyList())
            }
            (clothingItemsByType as MutableLiveData).value = groupedItems
        }
    }



    fun getClothingItemsByTypeId(typeId: Long): LiveData<List<ClothingItem>> {
        return clothingRepo.getClothingItemsByTypeId(typeId)
    }

    suspend fun updateOutfit(outfit: Outfit) {
        outfitRepo.update(outfit)
        _currentOutfit.value = outfit
    }

    fun deleteOutfit(outfit: Outfit) {
        viewModelScope.launch {
            outfitRepo.delete(outfit)
        }
        clearOutfitData()
    }

    fun setCurrentOutfit(outfit: Outfit) {
        _currentOutfit.value = outfit
    }

    fun setSelectedItems(items: List<ClothingItem>) {
        _selectedItems.value = items
    }

    fun removeClothingItem(item: ClothingItem) {
        _selectedItems.value = _selectedItems.value?.filter { it.clothingItemId != item.clothingItemId }
    }

    suspend fun getOutfitById(id: Long): Outfit? {
        return outfitRepo.getById(id)
    }

    fun getClothingItemsByOutfitId(outfitId: Long): LiveData<List<OutfitWithClothingItems>> {
        return joinRepo.getOutfitWithClothingItems(outfitId)
    }

    fun addClothingItem(item: ClothingItem) {
        _selectedItems.value = _selectedItems.value.orEmpty() + item
    }

    fun getClothingItemsByType(type: Long): LiveData<List<ClothingItem>> {
        return clothingRepo.getClothingItemsByTypeId(type)
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

    fun saveOutfitAndClear() {
        viewModelScope.launch {
            val outfit = _currentOutfit.value ?: return@launch
            val items = _selectedItems.value ?: emptyList()

            // Save relations
            items.forEach { item ->
                joinRepo.insert(
                    OutfitClothingItemCrossRef(
                    outfitId = outfit.outfitId,
                    clothingItemId = item.clothingItemId
                )
                )
            }
            // Save outfit
            outfitRepo.update(outfit)

            // Clear data after saving
            clearOutfitData()
        }
    }

    fun saveOutfit() {
        viewModelScope.launch {
            val outfit = _currentOutfit.value ?: return@launch
            outfitRepo.update(outfit)
        }
    }

    fun getOutfitColors(outfitId: Long): LiveData<List<Color>> {
        return colorRepository.getOutfitColors(outfitId)
    }

    fun getAllColors(): LiveData<List<Color>> {
        return colorRepository.getAllColors()
    }

    fun getAllAttributes(): LiveData<List<Attribute>> {
        return attributeRepository.getAllAttributes()
    }

    suspend fun addColorToOutfit(outfitId: Long, colorId: Long) {
        colorRepository.addColorToOutfit(outfitId, colorId)
    }

    suspend fun deleteColorFromOutfit(outfitId: Long, colorId: Long) {
        colorRepository.deleteColorFromOutfit(outfitId, colorId)
    }

    fun getOutfitAttributes(outfitId: Long): LiveData<List<Attribute>> {
        return attributeRepository.getAttributesForOutfit(outfitId)
    }

    suspend fun addAttributeToOutfit(outfitId: Long, attributeId: Long) {
        attributeRepository.addAttributeToOutfit(
            OutfitAttributeCrossRef(
                outfitId = outfitId,
                attributeId = attributeId
            )
        )
    }

    suspend fun deleteAttributeFromOutfit(outfitId: Long, attributeId: Long) {
        attributeRepository.deleteAttributeFromOutfit(
            OutfitAttributeCrossRef(
                outfitId = outfitId,
                attributeId = attributeId
            )
        )
    }

    fun createNewAttribute(name: String) {
        viewModelScope.launch {
            val newAttribute = Attribute(name = name)
            val attributeId = attributeRepository.insertAttribute(newAttribute)
        }
    }

    fun removeClothingItemFromOutfit(item: ClothingItem) {
        viewModelScope.launch {
            joinRepo.delete(OutfitClothingItemCrossRef(_currentOutfit.value!!.outfitId, item.clothingItemId))
            _selectedItems.value = _selectedItems.value?.filter { it.clothingItemId != item.clothingItemId }
        }
    }

    fun insertColor(color: Color) {
        viewModelScope.launch {
            colorRepository.insertColor(color)
        }
    }
}
