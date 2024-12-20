package com.example.closet.viewModels

import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.closet.objects.ClothingItem

class OutfitAddViewModel : ViewModel() {
    val items: MutableList<ClothingItem> = mutableListOf()
    var outfitImageUri: String? = null
}