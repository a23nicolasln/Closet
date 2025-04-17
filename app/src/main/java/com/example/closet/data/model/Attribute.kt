package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attribute(
    @PrimaryKey(autoGenerate = true) val attributeId: Long = 0L,
    val name: String
)

