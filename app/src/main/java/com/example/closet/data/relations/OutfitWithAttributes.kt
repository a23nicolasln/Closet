package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.Outfit

data class OutfitWithAttributes(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfitId",
        entityColumn = "attributeId",
        associateBy = Junction(OutfitAttributeCrossRef::class)
    )
    val attributes: List<Attribute>
)
