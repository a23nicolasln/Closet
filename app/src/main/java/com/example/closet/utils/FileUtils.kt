package com.example.closet.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

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

    suspend fun uploadImageToFirebase(context: Context, imagePath: String): String? = suspendCancellableCoroutine { cont ->
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            cont.resume(null, null)
            return@suspendCancellableCoroutine
        }

        val file = File(imagePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val imageRef = FirebaseStorage.getInstance().reference
            .child("user_images/$userId/$fileName")

        context.grantUriPermission(
            "com.google.android.gms",
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                cont.resume(uri.toString(), null)
            }.addOnFailureListener {
                cont.resume(null, null)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            cont.resume(null, null)
        }
    }





}
