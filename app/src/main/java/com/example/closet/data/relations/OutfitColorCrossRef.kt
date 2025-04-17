package com.example.closet.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["outfitId", "colorId"])
data class OutfitColorCrossRef(
    val outfitId: Long,
    val colorId: Long
)
