package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Color
import com.example.closet.data.model.Outfit

data class ColorWithOutfits(
    @Embedded val color: Color,
    @Relation(
        parentColumn = "colorId",
        entityColumn = "outfitId",
        associateBy = Junction(OutfitColorCrossRef::class)
    )
    val outfits: List<Outfit>
)
