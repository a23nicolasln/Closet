package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Color
import com.example.closet.data.model.Outfit

data class OutfitWithColors(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfitId",
        entityColumn = "colorId",
        associateBy = Junction(OutfitColorCrossRef::class)
    )
    val colors: List<Color>
)
