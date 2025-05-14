package com.example.closet.data.firebase.dto

data class OutfitDTO(
    var outfitId: Long = 0,
    var name: String = "",
    var imageUrl: String = "",
    var clothingItems: List<ClothingItemDTO> = emptyList(),
    var userId: String = "",
)
