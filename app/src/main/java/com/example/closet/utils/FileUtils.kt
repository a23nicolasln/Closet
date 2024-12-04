package com.example.closet.utils

import android.content.Context
import android.content.Intent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {
    fun saveImageToInternalStorage(context: Context, imageName: String, imageData: Intent): String? {
        val file = File(context.filesDir, imageName)
        try {
            val inputStream = context.contentResolver.openInputStream(imageData.data!!)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file.absolutePath
    }

    fun deleteImageFromInternalStorage(context: Context, imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete()
        }
    }
}