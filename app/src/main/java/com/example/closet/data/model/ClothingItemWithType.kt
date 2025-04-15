package com.example.closet.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ClothingItemWithType(
    @Embedded val clothingItem: ClothingItem,

    @Relation(
        parentColumn = "typeOwnerId",
        entityColumn = "typeId"
    )
    val type: Type
)
