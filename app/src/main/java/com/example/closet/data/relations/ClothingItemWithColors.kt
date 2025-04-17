package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color

data class ClothingItemWithColors(
    @Embedded val clothingItem: ClothingItem,
    @Relation(
        parentColumn = "clothingItemId",
        entityColumn = "colorId",
        associateBy = Junction(ClothingItemColorCrossRef::class)
    )
    val colors: List<Color>
)
