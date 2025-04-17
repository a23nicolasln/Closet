package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem

data class ClothingItemWithAttributes(
    @Embedded val clothingItem: ClothingItem,
    @Relation(
        parentColumn = "clothingItemId",
        entityColumn = "attributeId",
        associateBy = Junction(ClothingItemAttributeCrossRef::class)
    )
    val attributes: List<Attribute>
)
