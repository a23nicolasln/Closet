package com.example.closet.dao

import android.content.Context
import com.example.closet.objects.Outfit
import com.example.closet.utils.FileUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class DaoOutfit(private val context: Context) {
    private val gson: Gson = Gson()
    private val filePath: String = "outfits.json"

    private fun getFile(): File {
        return File(context.filesDir, filePath)
    }

    fun getAllOutfits(): List<Outfit> {
        val file = getFile()
        if (!file.exists()) {
            return emptyList()
        }
        val reader = InputStreamReader(file.inputStream())
        val outfitsType = object : TypeToken<List<Outfit>>() {}.type
        return try {
            gson.fromJson(reader, outfitsType) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        } finally {
            reader.close()
        }
    }

    fun saveOutfit(outfit: Outfit) {
        val outfits = getAllOutfits().toMutableList()
        outfits.add(outfit)
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(outfits, writer)
        writer.close()
    }

    fun saveOutfits(outfits: List<Outfit>) {
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(outfits, writer)
        writer.close()
    }

    fun deleteOutfit(outfit: Outfit) {
        val outfits = getAllOutfits().toMutableList()
        outfits.remove(outfit)
        val file = getFile()
        val writer = OutputStreamWriter(file.outputStream())
        gson.toJson(outfits, writer)
        writer.close()

        // Delete the image file
        FileUtils.deleteImageFromInternalStorage(context, outfit.imageUrl)
    }

    fun updateOutfit(outfit: Outfit) {
        val outfits = getAllOutfits().toMutableList()
        val index = outfits.indexOfFirst { it.id == outfit.id }
        if (index != -1) {
            outfits[index] = outfit
            val file = getFile()
            val writer = OutputStreamWriter(file.outputStream())
            gson.toJson(outfits, writer)
            writer.close()
        }
    }

    fun getOutfitById(id: String): Outfit? {
        return getAllOutfits().find { it.id == id }
    }
}