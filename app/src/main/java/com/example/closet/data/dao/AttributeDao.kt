package com.example.closet.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.closet.data.model.*
import com.example.closet.data.relations.*

@Dao
interface AttributeDao {

    // Insert single attribute
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attribute: Attribute)

    // Insert cross-ref for ClothingItem
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingItemAttributeCrossRef(crossRef: ClothingItemAttributeCrossRef)

    // Insert cross-ref for Outfit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitAttributeCrossRef(crossRef: OutfitAttributeCrossRef)

    // Get all attributes for a ClothingItem
    @Transaction
    @Query("""
        SELECT * FROM Attribute 
        WHERE attributeId IN (
            SELECT attributeId 
            FROM ClothingItemAttributeCrossRef 
            WHERE clothingItemId = :clothingItemId
        )
    """)
    fun getAttributesForClothingItem(clothingItemId: Long): LiveData<List<Attribute>>

    // Get all attributes for an Outfit
    @Transaction
    @Query("""
        SELECT * FROM Attribute 
        WHERE attributeId IN (
            SELECT attributeId 
            FROM OutfitAttributeCrossRef 
            WHERE outfitId = :outfitId
        )
    """)
    fun getAttributesForOutfit(outfitId: Long): LiveData<List<Attribute>>

    // Get ClothingItems by Attribute
    @Transaction
    @Query("""
        SELECT * FROM ClothingItem 
        WHERE clothingItemId IN (
            SELECT clothingItemId 
            FROM ClothingItemAttributeCrossRef 
            WHERE attributeId = :attributeId
        )
    """)
    fun getClothingItemsByAttribute(attributeId: Long): LiveData<List<ClothingItem>>

    // Get Outfits by Attribute
    @Transaction
    @Query("""
        SELECT * FROM Outfit 
        WHERE outfitId IN (
            SELECT outfitId 
            FROM OutfitAttributeCrossRef 
            WHERE attributeId = :attributeId
        )
    """)
    fun getOutfitsByAttribute(attributeId: Long): LiveData<List<Outfit>>

    // Get ClothingItemWithAttributes
    @Transaction
    @Query("SELECT * FROM ClothingItem WHERE clothingItemId = :clothingItemId")
    fun getClothingItemWithAttributes(clothingItemId: Long): LiveData<ClothingItemWithAttributes>

    // Get OutfitWithAttributes
    @Transaction
    @Query("SELECT * FROM Outfit WHERE outfitId = :outfitId")
    fun getOutfitWithAttributes(outfitId: Long): LiveData<OutfitWithAttributes>

    // Get AttributeWithClothingItems
    @Transaction
    @Query("SELECT * FROM Attribute WHERE attributeId = :attributeId")
    fun getAttributeWithClothingItems(attributeId: Long): LiveData<AttributeWithClothingItems>

    // Get AttributeWithOutfits
    @Transaction
    @Query("SELECT * FROM Attribute WHERE attributeId = :attributeId")
    fun getAttributeWithOutfits(attributeId: Long): LiveData<AttributeWithOutfits>

    // Get all attributes
    @Transaction
    @Query("SELECT * FROM Attribute")
    fun getAllAttributes(): LiveData<List<Attribute>>

    @Transaction
    @Query("""
        SELECT * FROM Attribute 
        WHERE attributeId IN (
            SELECT attributeId 
            FROM ClothingItemAttributeCrossRef 
            WHERE clothingItemId = :clothingItemId
        )
    """)
    fun getClothingItemAttributes(clothingItemId: Long): LiveData<List<Attribute>>

    @Delete
    suspend fun deleteAttributeFromClothingItem(crossRef: ClothingItemAttributeCrossRef)

    @Delete
    suspend fun deleteAttributeFromOutfit(crossRef: OutfitAttributeCrossRef)

    @Delete
    suspend fun delete(attribute: Attribute)
}
