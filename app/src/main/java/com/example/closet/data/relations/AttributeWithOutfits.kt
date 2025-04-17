package com.example.closet.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.Outfit

data class AttributeWithOutfits(
    @Embedded val attribute: Attribute,
    @Relation(
        parentColumn = "attributeId",
        entityColumn = "outfitId",
        associateBy = Junction(OutfitAttributeCrossRef::class)
    )
    val outfits: List<Outfit>
)
