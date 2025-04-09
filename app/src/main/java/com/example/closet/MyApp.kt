package com.example.closet

import android.app.Application
import com.example.closet.data.database.AppDatabase
import com.example.closet.repository.*


class MyApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val clothingItemRepository by lazy {
        ClothingItemRepository(database.clothingItemDao())
    }

    val outfitRepository by lazy {
        OutfitRepository(database.outfitDao())
    }

    val outfitClothingItemRepository by lazy {
        OutfitClothingItemRepository(database.outfitClothingItemDao())
    }
}