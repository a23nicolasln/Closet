package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit

data class ClothingItemWithOutfits(
    @Embedded val clothingItem: ClothingItem,
    @Relation(
        parentColumn = "clothingItemId",
        entityColumn = "outfitId",
        associateBy = Junction(OutfitClothingItemCrossRef::class)
    )
    val outfits: List<Outfit>
)