package com.example.closet.data.firebase.dto

data class ClothingItemDTO(
    val clothingItemId: Long = 0L,
    val imgUrl: String = "",
    val typeId: Long = 0L,
    val colorIds: List<Long> = emptyList(),
    val attributeIds: List<Long> = emptyList()
)
