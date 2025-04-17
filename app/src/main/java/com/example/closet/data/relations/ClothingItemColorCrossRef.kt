package com.example.closet.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["clothingItemId", "colorId"])
data class ClothingItemColorCrossRef(
    val clothingItemId: Long,
    val colorId: Long
)