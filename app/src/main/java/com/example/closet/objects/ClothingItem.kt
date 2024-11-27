package com.example.closet.objects

import java.util.UUID

data class ClothingItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val brand: String,
    val color: List<String>,
    val size: String,
    val imageUrl: String
)