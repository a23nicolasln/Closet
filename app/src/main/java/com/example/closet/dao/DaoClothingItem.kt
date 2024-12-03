package com.example.closet.dao

import android.content.Context
import com.example.closet.objects.ClothingItem
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.example.closet.utils.FileUtils

class DaoClothingItem(private val context: Context) {
    private val gson: Gson = Gson()
    private val filePath: String = "clothingItems.json"

    private fun getFile(): File {
        return File(context.filesDir, filePath)
    }

    fun getAllClothingItems(): List<ClothingItem> {
        val file = getFile()
        if (!file.exists()) {
            return emptyList()
        }
        val reader = InputStreamReader(file.inputStream())
        val clothingItemsType = object : TypeToken<List<ClothingItem>>() {}.type
        return try {
            gson.fromJson(reader, clothingItemsType) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        } finally {
            reader.close()
        }
    }


    fun saveClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getAllClothingItems().toMutableList()
        clothingItems.add(clothingItem)
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun saveClothingItems(clothingItems: List<ClothingItem>) {
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun deleteClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getAllClothingItems().toMutableList()
        clothingItems.remove(clothingItem)
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(clothingItems, writer)
        writer.close()

        // Delete the image file
        FileUtils.deleteImageFromInternalStorage(context, clothingItem.imageUrl)
    }

    fun updateClothingItem(clothingItem: ClothingItem) {
        val clothingItems = getAllClothingItems().toMutableList()
        val index = clothingItems.indexOfFirst { it.id == clothingItem.id }
        clothingItems[index] = clothingItem
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(clothingItems, writer)
        writer.close()
    }

    fun getClothingByType(type: String): List<ClothingItem> {
        return getAllClothingItems().filter { it.type == type }
    }

    fun getClothingItemById(id: String): ClothingItem? {
        return getAllClothingItems().find { it.id == id }
    }
}