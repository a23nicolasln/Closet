package com.example.closet.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["clothingItemId", "attributeId"])
data class ClothingItemAttributeCrossRef(
    val clothingItemId: Long,
    val attributeId: Long
)
