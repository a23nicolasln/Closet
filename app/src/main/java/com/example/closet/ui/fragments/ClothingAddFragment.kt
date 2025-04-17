package com.example.closet.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Color
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.adapters.AttributeAdapter
import com.example.closet.ui.adapters.ColorAdapter
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.example.closet.utils.FileUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch
import java.util.UUID

class ClothingAddFragment : Fragment() {

    private var clothingType: Long? = null
    private var clothingId: Long? = null
    private var imagePath: String? = null
    private lateinit var imageViewClothing: ImageView
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var colorRepository: ColorRepository
    private lateinit var attributeRepository: AttributeRepository
    private lateinit var viewModel: ClothingAddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            clothingType = it.getLong("clothingType")
            clothingId = it.getLong("clothingId")
        }

        val db = AppDatabase.getDatabase(requireContext())
        clothingItemRepository = ClothingItemRepository(db.clothingItemDao())
        typeRepository = TypeRepository(db.typeDao())
        colorRepository = ColorRepository(db.colorDao())
        attributeRepository = AttributeRepository(db.attributeDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clothing_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewClothing = view.findViewById(R.id.imageViewClothing)

        val viewModelFactory = ViewModelFactory {
            ClothingAddViewModel(clothingItemRepository, typeRepository, colorRepository, attributeRepository)
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[ClothingAddViewModel::class.java]

        // Fetch the clothing item if ID is not null
        clothingId?.let {
            viewModel.getClothingItemById(it)
        }

        // Observe clothing item
        viewModel.currentItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                imagePath = it.imageUrl

                Glide.with(requireContext())
                    .load(it.imageUrl)
                    .centerCrop()
                    .into(imageViewClothing)
            }
        }

        imageViewClothing.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        //Back button to save the clothing item
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            val clothingItem = ClothingItem(
                clothingItemId = clothingId ?: 0L,
                typeOwnerId = clothingType ?: 0L,
                imageUrl = imagePath
                    ?: "file:///android_asset/clothingImages/default_clothingItem.jpg"
            )

            //viewModel.insertClothingItem(clothingItem)
            requireActivity().onBackPressed()
        }

        // Set up RecyclerView for colors
        lifecycleScope.launch {
            val colorList = viewModel.getClothingItemColors(clothingId ?: 0L)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_colors)
            val adapter = ColorAdapter(
                colorList,
                onColorClick = { colorItem ->
                    // Handle the color item click
                },
                onAddClick = {
                    lifecycleScope.launch {
                        val dialogView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.dialog_select_color, null)
                        val dialogRecyclerView =
                            dialogView.findViewById<RecyclerView>(R.id.recycler_view_color_picker)
                        val allColors =
                            viewModel.getAllColors() // You may need to make this suspend and wrap in a coroutine
                        val dialogAdapter = ColorAdapter(
                            allColors,
                            onColorClick = { selectedColor ->
                                Log.d("ColorDialog", "Selected: ${selectedColor.name}")
                                //dialog.dismiss()
                            },
                            onAddClick = {
                            },
                            colorBackground = "#000000"
                        )

                        dialogRecyclerView.layoutManager =
                            FlexboxLayoutManager(requireContext()).apply {
                                flexDirection = FlexDirection.ROW
                                flexWrap = FlexWrap.WRAP
                            }
                        dialogRecyclerView.adapter = dialogAdapter

                        val dialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .create()

                        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.show()
                    }
                },
                colorBackground = "#3B3B3B"
            )

            recyclerView.adapter = adapter

            recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }

        // Set up RecyclerView for attributes
        lifecycleScope.launch {
            val attributeList = viewModel.getClothingItemAttributes(clothingId ?: 0L)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_attributes)
            val adapter = AttributeAdapter(
                attributeList,
                onAttributeClick = { attribute ->
                    // Handle the attribute item click
                },
                onAddClick = {
                    // Handle the add button click
                }
            )

            recyclerView.adapter = adapter

            recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val imageName = "clothing_image_${UUID.randomUUID()}.jpg"
                imagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, data)

                Glide.with(requireContext())
                    .load(imagePath)
                    .centerCrop()
                    .into(imageViewClothing)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}
