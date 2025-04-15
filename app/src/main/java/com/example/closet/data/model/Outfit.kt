package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Outfit(
    @PrimaryKey(autoGenerate = true) var outfitId: Long = 0L,
    var name: String,
    var imageUrl: String
)
