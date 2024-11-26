package com.example.closet.dao

import android.content.Context
import com.example.closet.objects.ClothingItem
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class DaoClothingItem(private val context: Context) {
    private val gson: Gson = Gson()
    private val file: String = "clothing_items.json"

    fun getClothingItems(): List<ClothingItem> {
        val assetManager = context.assets
        val inputStream = assetManager.open(file)
        val reader = InputStreamReader(inputStream)
        val clothingItemsType = object : TypeToken<List<ClothingItem>>() {}.type
        return try {
            gson.fromJson(reader, clothingItemsType) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        }
    }

    fun saveClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getClothingItems().toMutableList()
        clothingItems.add(clothingItem)
        val outputStream = context.openFileOutput(file, Context.MODE_PRIVATE)
        val writer = OutputStreamWriter(outputStream)
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun deleteClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getClothingItems().toMutableList()
        clothingItems.remove(clothingItem)
        val outputStream = context.openFileOutput(file, Context.MODE_PRIVATE)
        val writer = OutputStreamWriter(outputStream)
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun updateClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getClothingItems().toMutableList()
        val index = clothingItems.indexOfFirst { it.id == clothingItem.id }
        clothingItems[index] = clothingItem
        val outputStream = context.openFileOutput(file, Context.MODE_PRIVATE)
        val writer = OutputStreamWriter(outputStream)
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun getClothingByType(type: String): List<ClothingItem> {
        return getClothingItems().filter { it.type == type }
    }
}