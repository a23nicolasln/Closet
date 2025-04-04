package com.example.closet.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["outfitId", "clothingItemId"])
data class OutfitClothingItemCrossRef(
    val outfitId: Long,
    val clothingItemId: Long
)