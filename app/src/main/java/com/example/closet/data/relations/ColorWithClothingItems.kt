package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color

data class ColorWithClothingItems(
    @Embedded val color: Color,
    @Relation(
        parentColumn = "colorId",
        entityColumn = "clothingItemId",
        associateBy = Junction(ClothingItemColorCrossRef::class)
    )
    val clothingItems: List<ClothingItem>
)

