package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Color(
    @PrimaryKey(autoGenerate = true) val colorId: Long = 0L,
    val name: String,
    val hexCode: String
)
