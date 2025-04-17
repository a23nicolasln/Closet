package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type

data class ClothingItemWithType(
    @Embedded val clothingItem: ClothingItem,

    @Relation(
        parentColumn = "typeOwnerId",
        entityColumn = "typeId"
    )
    val type: Type
)
