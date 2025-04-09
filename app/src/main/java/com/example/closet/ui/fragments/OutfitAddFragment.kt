package com.example.closet.ui.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.OutfitAddViewModel
import com.example.closet.utils.FileUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.InputStream
import java.util.UUID

class OutfitAddFragment : Fragment() {

    private var imagePath: String? = null
    private lateinit var imageViewOutfit: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_outfit_add, container, false)
        return view}}
        /*
        // ViewModel to manage the outfit data
        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[OutfitAddViewModel::class.java]

        // Navigation for back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            viewModel.items.clear()
            viewModel.outfitImageUri = null
            findNavController().navigate(OutfitAddFragmentDirections.actionOutfitAddToOutfits())
        }

        // Set the image view to the selected image
        imageViewOutfit = view.findViewById(R.id.imageViewOutfit)
        if (viewModel.outfitImageUri != null) {
            Glide.with(this).load(viewModel.outfitImageUri).into(imageViewOutfit)
        }

        // Select image button
        imageViewOutfit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }


        // Add the new clothing item to the list
        if (arguments?.getString("newClothingItemID") != "") {
            DaoClothingItem(requireContext()).getClothingItemById(arguments?.getString("newClothingItemID")!!)
                ?.let { viewModel.items.add(it) }
        }

        //Copy the items of the view model to a new list
        val items = viewModel.items.toMutableList()

        items.add(ClothingItem(id = "add", type = "", brand = "", color = listOf(), size = "", imageUrl = ""))

        // RecyclerView for items
        val recyclerViewItems = view.findViewById<RecyclerView>(R.id.recyclerViewItems)
        recyclerViewItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewItems.adapter = ClothingItemAdapter(items) { clothingItem ->
            if (clothingItem.id == "add") {
                findNavController().navigate(OutfitAddFragmentDirections.actionOutfitAddToOutfitAddTypeSelector())
            } else {
                findNavController().navigate(OutfitAddFragmentDirections.actionOutfitAddToClothingView(clothingItem.id))
            }
        }

        // Save button
        val saveButton = view.findViewById<TextView>(R.id.save_button)
        saveButton.setOnClickListener {
            val daoOutfit = com.example.closet.old.dao.DaoOutfit(requireContext())
            val outfit = Outfit(
                name = view.findViewById<TextView>(R.id.outfitName).text.toString(),
                clothingItems = viewModel.items,
                imageUrl = viewModel.outfitImageUri ?: "file:///android_asset/clothingImages/default_outfit.jpg"
            )
            daoOutfit.saveOutfit(outfit)
            viewModel.items.clear()
            viewModel.outfitImageUri = null
            findNavController().navigate(OutfitAddFragmentDirections.actionOutfitAddToOutfits())
        }*/

        return view
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val imageStream: InputStream? = requireContext().contentResolver.openInputStream(it)
                val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                imageViewOutfit.setImageBitmap(selectedImage)

                // Save the image to internal storage
                val imageName = "outfit_image_${UUID.randomUUID()}.jpg"
                imagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, data)
                viewModel.outfitImageUri = imagePath
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}*/