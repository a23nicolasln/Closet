package com.example.closet.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.MainActivity
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.Outfit
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.example.closet.utils.FileUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*
class OutfitAddFragment : Fragment() {
    private var imagePath: String? = null
    private lateinit var imageViewOutfit: ImageView

    private val sharedVM: OutfitCreationViewModel by activityViewModels {
        val application = requireActivity().application
        val database = AppDatabase.getDatabase(application)
        ViewModelFactory {
            OutfitCreationViewModel(
                outfitRepo = OutfitRepository(database.outfitDao()),
                clothingRepo = ClothingItemRepository(database.clothingItemDao()),
                joinRepo = OutfitClothingItemRepository(database.outfitClothingItemDao())
            )
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let { uri ->
                val imageStream = requireContext().contentResolver.openInputStream(uri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                imageViewOutfit.setImageBitmap(selectedImage)

                val imageName = "outfit_image_${UUID.randomUUID()}.jpg"
                imagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, result.data)

                // Use ViewModel's public method instead of direct value assignment
                sharedVM.currentOutfit.value?.let { currentOutfit ->
                    sharedVM.updateOutfit(currentOutfit.copy(imageUrl = imagePath ?: ""))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_add, container, false)
        imageViewOutfit = view.findViewById(R.id.imageViewOutfit)

        // Initialize new outfit if needed
        if (sharedVM.currentOutfit.value == null) {
            lifecycleScope.launch {
                sharedVM.createNewOutfit()
            }
        }

        // Observe outfit data with null checks
        sharedVM.currentOutfit.observe(viewLifecycleOwner) { outfit ->
            outfit?.let {
                if (it.imageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(it.imageUrl)
                        .placeholder(R.drawable.default_outfit)
                        .into(imageViewOutfit)
                }
                view.findViewById<TextView>(R.id.outfitName)?.text = it.name
            }
        }

        // Observe selected items with empty state handling
        sharedVM.selectedItems.observe(viewLifecycleOwner) { items ->
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewItems)
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.adapter = ClothingItemAdapter(items ?: emptyList()) { item ->
                findNavController().navigate(
                    OutfitAddFragmentDirections.actionOutfitAddToClothingView(item.clothingItemId)
                )
            }
        }

        setupButtons(view)
        return view
    }

    private fun setupButtons(view: View) {
        // Back button - clears ViewModel state
        view.findViewById<FloatingActionButton>(R.id.back_button).setOnClickListener {
            sharedVM.clearOutfitData()
            findNavController().navigateUp()
        }

        // Image picker
        imageViewOutfit.setOnClickListener {
            selectImageLauncher.launch(
                Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            )
        }

        // Add clothing item
        view.findViewById<Button>(R.id.add_clothing_button).setOnClickListener {
            findNavController().navigate(
                OutfitAddFragmentDirections.actionOutfitAddToOutfitAddTypeSelector()
            )
        }

        // Save button - handles null states
        view.findViewById<TextView>(R.id.save_button).setOnClickListener {
            lifecycleScope.launch {
                val outfitName = view.findViewById<TextView>(R.id.outfitName)?.text?.toString() ?: "New Outfit"
                sharedVM.currentOutfit.value?.let { current ->
                    sharedVM.updateOutfit(current.copy(name = outfitName))
                    // Save outfit with image path
                    sharedVM.currentOutfit.value?.let { outfit ->
                        if (imagePath != null) {
                            outfit.imageUrl = imagePath!!
                        }
                    }
                    // Save outfit to database
                    sharedVM.saveOutfit()
                    // Navigate back to main activity
                    findNavController().navigateUp()
                }
            }
        }
    }
}