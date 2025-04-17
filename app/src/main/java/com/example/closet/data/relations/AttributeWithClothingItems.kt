package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem

data class AttributeWithClothingItems(
    @Embedded val attribute: Attribute,
    @Relation(
        parentColumn = "attributeId",
        entityColumn = "clothingItemId",
        associateBy = Junction(ClothingItemAttributeCrossRef::class)
    )
    val clothingItems: List<ClothingItem>
)
