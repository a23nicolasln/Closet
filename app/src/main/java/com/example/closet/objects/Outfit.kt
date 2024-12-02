package com.example.closet.objects

import java.util.UUID

data class Outfit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val clothingItems: List<ClothingItem>,
    val imageUrl: String
)
