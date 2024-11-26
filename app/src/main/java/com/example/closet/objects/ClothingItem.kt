package com.example.closet.objects

data class ClothingItem(
    val id: String,
    val type: String,
    val color: List<String>,
    val size: String,
    val imageUrl: String
)