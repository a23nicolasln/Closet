package com.example.closet.data.firebase.dto

import com.example.closet.data.model.ClothingItem

data class OutfitDTO(
    val outfitId: Long = 0L,
    val name: String = "",
    val imageUrl: String = "",
    val clothingItems: List<ClothingItem> = emptyList(),
    val userId: String = "",
)
