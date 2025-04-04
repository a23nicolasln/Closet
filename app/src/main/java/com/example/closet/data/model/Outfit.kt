package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Outfit(
    @PrimaryKey(autoGenerate = true) val outfitId: Long = 0L,
    val name: String,
    val imageUrl: String
)
