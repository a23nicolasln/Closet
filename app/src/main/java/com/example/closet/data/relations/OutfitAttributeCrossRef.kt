package com.example.closet.data.relations

import androidx.room.Entity

@Entity(primaryKeys = ["outfitId", "attributeId"])
data class OutfitAttributeCrossRef(
    val outfitId: Long,
    val attributeId: Long
)
