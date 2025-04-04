package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClothingItem(
    @PrimaryKey(autoGenerate = true) val clothingItemId: Long = 0L,
    val type: String,
    val brand: String,
    val color: String,
    val size: String,
    val imageUrl: String
)