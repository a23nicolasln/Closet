package com.example.closet.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.ClothingItem
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    ): View {
        return inflater.inflate(R.layout.fragment_clothing_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewClothing = view.findViewById(R.id.imageViewClothing)

        val viewModelFactory = ViewModelFactory {
            ClothingAddViewModel(
                clothingItemRepository,
                typeRepository,
                colorRepository,
                attributeRepository
            )
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[ClothingAddViewModel::class.java]

        if (clothingId != null && clothingId != 0L) {
            viewModel.getClothingItemById(clothingId!!)
        } else {
            lifecycleScope.launch {
                clothingId = clothingItemRepository.insertClothingItem(
                    ClothingItem(
                        imageUrl = "",
                        typeOwnerId = clothingType ?: 0L
                    )
                )
                viewModel.getClothingItemById(clothingId!!)
            }
        }

        viewModel.currentItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                imagePath = it.imageUrl

                Glide.with(requireContext())
                    .load(it.imageUrl)
                    .centerCrop()
                    .into(imageViewClothing)

                setupColorRecyclerView(view)
                setupAttributeRecyclerView(view)
            }
        }


        imageViewClothing.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        // Back button
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            lifecycleScope.launch {
                val clothingItem = viewModel.currentItem.value
                if (clothingItem != null) {
                    clothingItem.imageUrl = imagePath.toString()
                    viewModel.updateClothingItem(clothingItem)
                }
                viewModel.clearCurrentItem()
            }
            findNavController().navigateUp()
        }

        // Delete button
        view.findViewById<FloatingActionButton>(R.id.delete_button).setOnClickListener {
            clothingId?.let {
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_confirmation, null)
                val dialogQuestion = dialogView.findViewById<TextView>(R.id.question_text)
                dialogQuestion.text = getString(R.string.delete_clothing_item)
                val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
                val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                acceptButton.setOnClickListener {
                    lifecycleScope.launch {
                        viewModel.deleteClothingItemById(clothingId!!)
                    }
                    FileUtils.deleteImageFromInternalStorage(requireContext(), imagePath.toString())
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        setupColorRecyclerView(view)
        setupAttributeRecyclerView(view)
    }

    private fun setupColorRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_colors)

        val adapter = ColorAdapter(
            emptyList(),
            onColorClick = { selectedColor -> confirmDeleteColor(selectedColor.colorId) },
            onAddClick = { selectColorDialog() },
            colorBackground = "#3B3B3B"
        )

        recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.adapter = adapter

        clothingId?.let {
            viewModel.getClothingItemColors(it)
                .observe(viewLifecycleOwner) { colorList ->
                    adapter.updateColors(colorList)
                }
        }
    }

    private fun setupAttributeRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_attributes)

        val adapter = AttributeAdapter(
            emptyList(),
            onAttributeClick = { attribute -> confirmDeleteAttribute(attribute.attributeId) },
            onAddClick = { selectAttributeDialog() },
            colorBackground = "#3B3B3B"
        )

        recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.adapter = adapter

        viewModel.getClothingItemAttributes(clothingId ?: 0L)
            .observe(viewLifecycleOwner) { attributeList ->
                adapter.updateAttributeList(attributeList)
            }
    }

    private fun confirmDeleteColor(colorId: Long) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_confirmation, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.question_text)
        dialogTitle.text = getString(R.string.remove_color)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.accept_button).setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteColorFromClothingItem(clothingId ?: 0L, colorId)
            }
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cancel_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun confirmDeleteAttribute(attributeId: Long) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_confirmation, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.question_text)
        dialogTitle.text = getString(R.string.remove_attribute)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.accept_button).setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteAttributeFromClothingItem(clothingId ?: 0L, attributeId)
            }
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cancel_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun selectColorDialog() {
        lifecycleScope.launch {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_select_color_attribute, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view_color_picker)

            val colors = viewModel.getAllColors()

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            recyclerView.adapter = ColorAdapter(
                emptyList(),
                onColorClick = { color ->
                    lifecycleScope.launch {
                        viewModel.addColorToClothingItem(clothingId ?: 0L, color.colorId)
                    }
                    dialog.dismiss()
                },
                onAddClick = {},
                colorBackground = "#000000"
            )

            colors.observe(viewLifecycleOwner) { colorList ->
                (recyclerView.adapter as ColorAdapter).updateColors(colorList)
            }

            dialog.show()
        }
    }

    private fun selectAttributeDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_select_color_attribute, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view_color_picker)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        val adapter = AttributeAdapter(
            emptyList(),
            onAttributeClick = { attribute ->
                lifecycleScope.launch {
                    viewModel.addAttributeToClothingItem(clothingId ?: 0L, attribute.attributeId)
                }
                dialog.dismiss()
            },
            onAddClick = {
                showAddAttributeDialog()
            },
            colorBackground = "#000000"
        )

        recyclerView.adapter = adapter

        // Observe the LiveData properly
        viewModel.getAllAttributes().observe(viewLifecycleOwner) { attributes ->
            adapter.updateAttributeList(attributes)
        }

        dialog.show()
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

    private fun showAddAttributeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_type, null)
        val input = dialogView.findViewById<EditText>(R.id.typeNameInput)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = getString(R.string.create_attribute)
        input.hint = getString(R.string.attribute_hint)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSave.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.createNewAttribute(name)
                dialog.dismiss()
            } else {
                input.error = "Attribute name can't be empty"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}
