package com.example.closet

import android.app.Application
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.google.firebase.FirebaseApp


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
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