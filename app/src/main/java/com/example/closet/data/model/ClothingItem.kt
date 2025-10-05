package com.example.closet.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Type::class,
        parentColumns = ["typeId"],
        childColumns = ["typeOwnerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["typeOwnerId"])]
)
data class ClothingItem(
    @PrimaryKey(autoGenerate = true) val clothingItemId: Long = 0L,
    val typeOwnerId: Long,
    var imageUrl: String,
    var link: String,
    var timestamp: Long = System.currentTimeMillis()
)