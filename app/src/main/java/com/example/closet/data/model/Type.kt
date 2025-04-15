package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Type (
    @PrimaryKey(autoGenerate = true) val typeId: Long,
    val name: String
)