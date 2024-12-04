package com.example.closet.presentation

import android.content.Context
import com.example.closet.objects.ClothingItem
import com.example.closet.objects.Outfit
import com.example.closet.utils.FileUtils
import java.util.UUID

class Presentation(private val context: Context) {

    fun getOutfits(): List<Outfit> {
        val outfits = mutableListOf<Outfit>()
        outfits.add(Outfits.outfit1)
        outfits.add(Outfits.outfit2)
        outfits.add(Outfits.outfit3)
        outfits.add(Outfits.outfit4)
        outfits.add(Outfits.outfit5)
        outfits.add(Outfits.outfit6)
        return outfits
    }

    fun getClothingItems(): List<ClothingItem> {
        val clothingItems = mutableListOf<ClothingItem>()
        clothingItems.add(ClothingItems.bracelet1)
        clothingItems.add(ClothingItems.bracelet2)
        clothingItems.add(ClothingItems.earring1)
        clothingItems.add(ClothingItems.earring2)
        clothingItems.add(ClothingItems.jacket1)
        clothingItems.add(ClothingItems.jacket2)
        clothingItems.add(ClothingItems.jacket3)
        clothingItems.add(ClothingItems.jacket4)
        clothingItems.add(ClothingItems.jumper1)
        clothingItems.add(ClothingItems.jumper2)
        clothingItems.add(ClothingItems.jumper3)
        clothingItems.add(ClothingItems.necklace1)
        clothingItems.add(ClothingItems.necklace2)
        clothingItems.add(ClothingItems.ring1)
        clothingItems.add(ClothingItems.ring2)
        clothingItems.add(ClothingItems.shoes1)
        clothingItems.add(ClothingItems.shoes2)
        clothingItems.add(ClothingItems.shoes3)
        clothingItems.add(ClothingItems.trousers1)
        clothingItems.add(ClothingItems.trousers2)
        clothingItems.add(ClothingItems.trousers3)
        clothingItems.add(ClothingItems.trousers4)
        clothingItems.add(ClothingItems.tShirt1)
        clothingItems.add(ClothingItems.tShirt2)
        clothingItems.add(ClothingItems.tShirt3)
        return clothingItems
    }
}

object ClothingItems {
    val bracelet1 = ClothingItem(id = "bracelet1", type = "Bracelet", brand = "BrandA", color = listOf("Gold"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/bracelet1.jpg")
    val bracelet2 = ClothingItem(id = "bracelet2", type = "Bracelet", brand = "BrandB", color = listOf("Silver"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/bracelet2.jpg")
    val earring1 = ClothingItem(id = "earring1", type = "Earring", brand = "BrandC", color = listOf("Gold"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/earring1.jpg")
    val earring2 = ClothingItem(id = "earring2", type = "Earring", brand = "BrandD", color = listOf("Silver"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/earring2.jpg")
    val jacket1 = ClothingItem(id = "jacket1", type = "Jacket", brand = "BrandE", color = listOf("Black"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/jacket1.jpg")
    val jacket2 = ClothingItem(id = "jacket2", type = "Jacket", brand = "BrandF", color = listOf("Blue"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/jacket2.jpg")
    val jacket3 = ClothingItem(id = "jacket3", type = "Jacket", brand = "BrandG", color = listOf("Red"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/jacket3.jpg")
    val jacket4 = ClothingItem(id = "jacket4", type = "Jacket", brand = "BrandH", color = listOf("Green"), size = "XL", imageUrl = "/data/user/0/com.example.closet/files/jacket4.jpg")
    val jumper1 = ClothingItem(id = "jumper1", type = "Jumper", brand = "BrandI", color = listOf("Yellow"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/jumper1.jpg")
    val jumper2 = ClothingItem(id = "jumper2", type = "Jumper", brand = "BrandJ", color = listOf("Purple"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/jumper2.jpg")
    val jumper3 = ClothingItem(id = "jumper3", type = "Jumper", brand = "BrandK", color = listOf("Orange"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/jumper3.jpg")
    val necklace1 = ClothingItem(id = "necklace1", type = "Necklace", brand = "BrandL", color = listOf("Gold"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/necklace1.jpg")
    val necklace2 = ClothingItem(id = "necklace2", type = "Necklace", brand = "BrandM", color = listOf("Silver"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/necklace2.jpg")
    val ring1 = ClothingItem(id = "ring1", type = "Ring", brand = "BrandN", color = listOf("Gold"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/ring1.jpg")
    val ring2 = ClothingItem(id = "ring2", type = "Ring", brand = "BrandO", color = listOf("Silver"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/ring2.jpg")
    val shoes1 = ClothingItem(id = "shoes1", type = "Shoes", brand = "BrandP", color = listOf("Black"), size = "42", imageUrl = "/data/user/0/com.example.closet/files/shoes1.jpg")
    val shoes2 = ClothingItem(id = "shoes2", type = "Shoes", brand = "BrandQ", color = listOf("White"), size = "43", imageUrl = "/data/user/0/com.example.closet/files/shoes2.jpg")
    val shoes3 = ClothingItem(id = "shoes3", type = "Shoes", brand = "BrandR", color = listOf("Brown"), size = "44", imageUrl = "/data/user/0/com.example.closet/files/shoes3.jpg")
    val trousers1 = ClothingItem(id = "trousers1", type = "Trousers", brand = "BrandS", color = listOf("Black"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/trousers1.jpg")
    val trousers2 = ClothingItem(id = "trousers2", type = "Trousers", brand = "BrandT", color = listOf("Blue"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/trousers2.jpg")
    val trousers3 = ClothingItem(id = "trousers3", type = "Trousers", brand = "BrandU", color = listOf("Grey"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/trousers3.jpg")
    val trousers4 = ClothingItem(id = "trousers4", type = "Trousers", brand = "BrandV", color = listOf("Green"), size = "XL", imageUrl = "/data/user/0/com.example.closet/files/trousers4.jpg")
    val tShirt1 = ClothingItem(id = "tShirt1", type = "T-shirt", brand = "BrandW", color = listOf("Red"), size = "M", imageUrl = "/data/user/0/com.example.closet/files/t_shirt1.jpg")
    val tShirt2 = ClothingItem(id = "tShirt2", type = "T-shirt", brand = "BrandX", color = listOf("Yellow"), size = "L", imageUrl = "/data/user/0/com.example.closet/files/t_shirt2.jpg")
    val tShirt3 = ClothingItem(id = "tShirt3", type = "T-shirt", brand = "BrandY", color = listOf("Blue"), size = "S", imageUrl = "/data/user/0/com.example.closet/files/t_shirt3.jpg")
}

object Outfits {
    val outfit1 = Outfit(
        id = "outfit1",
        name = "Casual Outfit",
        clothingItems = listOf(
            ClothingItems.jacket1,
            ClothingItems.tShirt1,
            ClothingItems.trousers1,
            ClothingItems.shoes1
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit1.jpeg"
    )

    val outfit2 = Outfit(
        id = "outfit2",
        name = "Formal Outfit",
        clothingItems = listOf(
            ClothingItems.jacket2,
            ClothingItems.tShirt2,
            ClothingItems.trousers2,
            ClothingItems.shoes2
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit2.jpeg"
    )

    val outfit3 = Outfit(
        id = "outfit3",
        name = "Sporty Outfit",
        clothingItems = listOf(
            ClothingItems.jacket3,
            ClothingItems.tShirt3,
            ClothingItems.trousers3,
            ClothingItems.shoes3
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit3.jpeg"
    )

    val outfit4 = Outfit(
        id = "outfit4",
        name = "Winter Outfit",
        clothingItems = listOf(
            ClothingItems.jacket4,
            ClothingItems.jumper1,
            ClothingItems.trousers4,
            ClothingItems.shoes1
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit4.jpeg"
    )

    val outfit5 = Outfit(
        id = "outfit5",
        name = "Summer Outfit",
        clothingItems = listOf(
            ClothingItems.jumper2,
            ClothingItems.tShirt1,
            ClothingItems.trousers1,
            ClothingItems.shoes2
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit5.jpeg"
    )

    val outfit6 = Outfit(
        id = "outfit6",
        name = "Party Outfit",
        clothingItems = listOf(
            ClothingItems.jumper3,
            ClothingItems.tShirt2,
            ClothingItems.trousers2,
            ClothingItems.shoes3
        ),
        imageUrl = "/data/user/0/com.example.closet/files/outfit6.jpeg"
    )
}