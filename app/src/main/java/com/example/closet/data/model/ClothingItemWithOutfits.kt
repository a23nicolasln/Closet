package com.example.closet.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ClothingItemWithOutfits(
    @Embedded val clothingItem: ClothingItem,
    @Relation(
        parentColumn = "clothingItemId",
        entityColumn = "outfitId",
        associateBy = Junction(OutfitClothingItemCrossRef::class)
    )
    val outfits: List<Outfit>
)