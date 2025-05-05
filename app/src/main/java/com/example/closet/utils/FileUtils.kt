package com.example.closet.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {

    fun saveImageToInternalStorage(context: Context, imageName: String, imageData: Intent?): String? {
        val uri: Uri = imageData?.data ?: return null
        val file = File(context.filesDir, imageName)

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImageFromInternalStorage(context: Context, imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveDrawableToInternalStorage(context: Context, drawableResId: Int, filename: String): String? {
        return try {
            // Load the drawable as a Bitmap
            val drawable = ContextCompat.getDrawable(context, drawableResId)
            val bitmap = (drawable as BitmapDrawable).bitmap

            // Create a file in internal storage
            val file = File(context.filesDir, filename)
            val outputStream = FileOutputStream(file)

            // Compress and write the bitmap to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Return the file path
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
