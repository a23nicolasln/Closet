package com.example.closet.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["outfitId", "clothingItemId"])
data class OutfitClothingItemCrossRef(
    val outfitId: Long,
    val clothingItemId: Long
)