package com.example.closet.data.relations


import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit

data class OutfitWithClothingItems(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfitId",
        entityColumn = "clothingItemId",
        associateBy = Junction(OutfitClothingItemCrossRef::class)
    )
    val clothingItems: List<ClothingItem>
)