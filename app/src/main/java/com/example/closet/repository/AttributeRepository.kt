package com.example.closet.repository

import androidx.lifecycle.LiveData
import com.example.closet.data.dao.AttributeDao
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit
import com.example.closet.data.relations.*

class AttributeRepository(private val attributeDao: AttributeDao) {

    // Insert a new attribute
    suspend fun insertAttribute(attribute: Attribute) {
        attributeDao.insert(attribute)
    }

    // Add an attribute to a clothing item
    suspend fun addAttributeToClothingItem(crossRef: ClothingItemAttributeCrossRef) {
        attributeDao.insertClothingItemAttributeCrossRef(crossRef)
    }

    // Add an attribute to an outfit
    suspend fun addAttributeToOutfit(crossRef: OutfitAttributeCrossRef) {
        attributeDao.insertOutfitAttributeCrossRef(crossRef)
    }

    // Get attributes for a clothing item
    fun getAttributesForClothingItem(clothingItemId: Long): LiveData<List<Attribute>> {
        return attributeDao.getAttributesForClothingItem(clothingItemId)
    }

    // Get attributes for an outfit
    fun getAttributesForOutfit(outfitId: Long): LiveData<List<Attribute>> {
        return attributeDao.getAttributesForOutfit(outfitId)
    }

    // Get clothing items by attribute
    fun getClothingItemsByAttribute(attributeId: Long): LiveData<List<ClothingItem>> {
        return attributeDao.getClothingItemsByAttribute(attributeId)
    }

    // Get outfits by attribute
    fun getOutfitsByAttribute(attributeId: Long): LiveData<List<Outfit>> {
        return attributeDao.getOutfitsByAttribute(attributeId)
    }

    // Get ClothingItem with its attributes
    fun getClothingItemWithAttributes(clothingItemId: Long): LiveData<ClothingItemWithAttributes> {
        return attributeDao.getClothingItemWithAttributes(clothingItemId)
    }

    // Get Outfit with its attributes
    fun getOutfitWithAttributes(outfitId: Long): LiveData<OutfitWithAttributes> {
        return attributeDao.getOutfitWithAttributes(outfitId)
    }

    // Get Attribute with its clothing items
    fun getAttributeWithClothingItems(attributeId: Long): LiveData<AttributeWithClothingItems> {
        return attributeDao.getAttributeWithClothingItems(attributeId)
    }

    // Get Attribute with its outfits
    fun getAttributeWithOutfits(attributeId: Long): LiveData<AttributeWithOutfits> {
        return attributeDao.getAttributeWithOutfits(attributeId)
    }

    // Get all attributes
    fun getAllAttributes(): LiveData<List<Attribute>> {
        return attributeDao.getAllAttributes()
    }

    fun getClothingItemAttributes(clothingItemId: Long): LiveData<List<Attribute>> {
        return attributeDao.getClothingItemAttributes(clothingItemId)
    }

    suspend fun deleteAttributeFromClothingItem(crossRef: ClothingItemAttributeCrossRef) {
        attributeDao.deleteAttributeFromClothingItem(crossRef)
    }

    suspend fun deleteAttributeFromOutfit(crossRef: OutfitAttributeCrossRef) {
        attributeDao.deleteAttributeFromOutfit(crossRef)
    }

    suspend fun delete(attribute: Attribute) {
        attributeDao.delete(attribute)
    }
}
