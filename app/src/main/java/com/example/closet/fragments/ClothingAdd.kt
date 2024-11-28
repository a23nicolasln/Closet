package com.example.closet.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.closet.R
import com.example.closet.dao.DaoClothingItem
import com.example.closet.objects.ClothingItem
import com.example.closet.utils.FileUtils
import com.google.android.material.textfield.TextInputEditText
import java.io.InputStream
import java.util.UUID

class ClothingAdd : Fragment() {

    private var clothingType: String? = null
    private var imagePath: String? = null
    private lateinit var imageViewClothing: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clothingType = it.getString("clothingType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_clothing_add, container, false)

        //Set the image view to the selected image
        imageViewClothing = view.findViewById(R.id.imageViewClothing)
        imageViewClothing.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        // Back button
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Save button
        val saveButton = view.findViewById<View>(R.id.save_button)
        saveButton.setOnClickListener {
            // Save the clothing item
            val clothingItem = ClothingItem(
                type = clothingType ?: "",
                brand = view.findViewById<TextInputEditText>(R.id.brand).text.toString(),
                color = view.findViewById<TextInputEditText>(R.id.colors).text.toString().split(","),
                size = view.findViewById<TextInputEditText>(R.id.size).text.toString(),
                imageUrl = imagePath ?: "file:///android_asset/clothingImages/default_clothingItem.jpg"
            )
            val daoClothingItem = DaoClothingItem(requireContext())
            daoClothingItem.saveClothingItem(clothingItem)

            // Navigate back to the previous fragment
            requireActivity().onBackPressed()

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val imageStream: InputStream? = requireContext().contentResolver.openInputStream(it)
                val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                imageViewClothing.setImageBitmap(selectedImage)

                // Save the image to internal storage
                val imageName = "clothing_image_${UUID.randomUUID()}.jpg"
                imagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, data)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}