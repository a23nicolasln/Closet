package com.example.closet.objects

data class Outfit(
    val id: String,
    val name: String,
    val clothingItems: List<ClothingItem>,
    val imageUrl: String
)