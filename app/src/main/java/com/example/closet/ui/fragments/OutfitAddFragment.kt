package com.example.closet.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.Color
import com.example.closet.data.model.Type
import com.example.closet.data.repository.AttributeRepository
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.ColorRepository
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.data.repository.TypeRepository
import com.example.closet.ui.adapters.AttributeAdapter
import com.example.closet.ui.adapters.ClothingItemAdapterSmall
import com.example.closet.ui.adapters.ColorAdapter
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.example.closet.utils.FileUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import kotlinx.coroutines.launch
import java.util.*
class OutfitAddFragment : Fragment() {
    private var imagePath: String? = null
    private lateinit var imageViewOutfit: ImageView
    private val defaultImageResId = R.drawable.icon_add


    private val sharedVM: OutfitCreationViewModel by activityViewModels {
        val application = requireActivity().application
        val database = AppDatabase.getDatabase(application)
        ViewModelFactory {
            OutfitCreationViewModel(
                outfitRepo = OutfitRepository(database.outfitDao()),
                clothingRepo = ClothingItemRepository(database.clothingItemDao()),
                joinRepo = OutfitClothingItemRepository(database.outfitClothingItemDao()),
                typeRepo = TypeRepository(database.typeDao()),
                colorRepository = ColorRepository(database.colorDao()),
                attributeRepository = AttributeRepository(database.attributeDao())
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
                val newImagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, result.data)

                lifecycleScope.launch {
                    sharedVM.currentOutfit.value?.let { outfit ->
                        // Delete previous image if it's not empty and not the default
                        if (outfit.imageUrl.isNotEmpty()) {
                            FileUtils.deleteImageFromInternalStorage(requireContext(), outfit.imageUrl)
                        }
                        imagePath = newImagePath
                        newImagePath?.let { outfit.copy(imageUrl = it) }
                            ?.let { sharedVM.updateOutfit(it) }
                    }
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
            //check if the outfitId from the arguments is not null
            val outfitId = OutfitAddFragmentArgs.fromBundle(requireArguments()).outfitId
            if (outfitId != 0L) {
                lifecycleScope.launch {
                    sharedVM.getOutfitById(outfitId)
                    val items = sharedVM.getClothingItemsByOutfitId(outfitId)
                    items.observe(viewLifecycleOwner) { outfitWithClothingItems ->
                        val clothingItems = outfitWithClothingItems.flatMap { it.clothingItems }
                        sharedVM.setSelectedItems(clothingItems)
                    }
                }
                lifecycleScope.launch {
                    sharedVM.getOutfitById(outfitId)?.let { outfit ->
                        sharedVM.setCurrentOutfit(outfit)
                        if (outfit.imageUrl.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(outfit.imageUrl)
                                .placeholder(R.drawable.icon_loading)
                                .into(imageViewOutfit)
                        } else {
                            imageViewOutfit.setImageResource(defaultImageResId)
                        }

                    }
                }
                view.findViewById<TextView>(R.id.outfitName)?.text = sharedVM.currentOutfit.value?.name
            } else {
                lifecycleScope.launch {
                    sharedVM.createNewOutfit()
                }
            }

        }

        // Observe outfit data with null checks
        sharedVM.currentOutfit.observe(viewLifecycleOwner) { outfit ->
            outfit?.let {
                if (it.imageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(it.imageUrl)
                        .placeholder(defaultImageResId)
                        .into(imageViewOutfit)
                } else {
                    imageViewOutfit.setImageResource(defaultImageResId)
                }
                view.findViewById<TextView>(R.id.outfitName)?.text = it.name
            }
        }


        // Observe selected Clothing items
        sharedVM.selectedItems.observe(viewLifecycleOwner) { items ->
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewItems)
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyclerView.adapter = ClothingItemAdapterSmall(items ?: emptyList()) { item ->
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_confirmation, null)
                val dialogTitle = dialogView.findViewById<TextView>(R.id.question_text)
                dialogTitle.text = getString(R.string.remove_item)
                val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
                val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                acceptButton.setOnClickListener {
                    lifecycleScope.launch {
                        sharedVM.removeClothingItemFromOutfit(item)
                    }
                    dialog.dismiss()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        setupButtons(view)
        return view
    }

    private fun setupButtons(view: View) {
        // Back button - clears ViewModel state and saves outfit
        view.findViewById<FloatingActionButton>(R.id.back_button).setOnClickListener {
            lifecycleScope.launch {
                val outfitName = view.findViewById<TextView>(R.id.outfitName)?.text?.toString() ?: ""
                sharedVM.currentOutfit.value?.let { current ->
                    sharedVM.updateOutfit(current.copy(name = outfitName))
                    // Save outfit with image path
                    sharedVM.currentOutfit.value?.let { outfit ->
                        if (imagePath != null) {
                            outfit.imageUrl = imagePath!!
                        }
                    }
                    // Save outfit to database
                    sharedVM.saveOutfitAndClear()
                    findNavController().navigate(R.id.action_outfitAdd_to_outfits)
                }
            }
        }

        // Image picker
        imageViewOutfit.setOnClickListener {
            val outfitName = view.findViewById<TextView>(R.id.outfitName)?.text?.toString() ?: ""
            lifecycleScope.launch {
                sharedVM.currentOutfit.value?.let { current ->
                    sharedVM.updateOutfit(current.copy(name = outfitName))
                }
            }
            selectImageLauncher.launch(
                Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            )
        }

        // Add clothing item
        view.findViewById<TextView>(R.id.add_clothing_button).setOnClickListener {
            val outfitName = view.findViewById<TextView>(R.id.outfitName)?.text?.toString() ?: ""
            lifecycleScope.launch {
                sharedVM.currentOutfit.value?.let { current ->
                    sharedVM.updateOutfit(current.copy(name = outfitName))
                }
            }


            findNavController().navigate(
                OutfitAddFragmentDirections.actionOutfitAddToOutfitAddTypeSelector()
            )
        }

        // Delete button with dialog confirmation
        view.findViewById<FloatingActionButton>(R.id.delete_button).setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_confirmation, null)
            val dialogQuestion = dialogView.findViewById<TextView>(R.id.question_text)
            dialogQuestion.text = getString(R.string.delete_outfit)
            val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
            val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            acceptButton.setOnClickListener {
                lifecycleScope.launch {
                    sharedVM.currentOutfit.value?.let { current ->
                        sharedVM.deleteOutfit(current)
                        sharedVM.clearOutfitData()
                    }
                }
                imagePath?.let { path ->
                    FileUtils.deleteImageFromInternalStorage(requireContext(), path)
                }
                dialog.dismiss()
                findNavController().navigateUp()
            }
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        //Set up colors recycled view
        lifecycleScope.launch {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_colors)
            val adapter = ColorAdapter(
                emptyList(),
                onColorClick = { selectedColor ->
                    val dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_confirmation, null)
                    val dialogTitle = dialogView.findViewById<TextView>(R.id.question_text)
                    dialogTitle.text = getString(R.string.remove_color)
                    val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
                    val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .create()
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    acceptButton.setOnClickListener {
                        lifecycleScope.launch {
                            sharedVM.currentOutfit.value?.let { outfit ->
                                sharedVM.deleteColorFromOutfit(outfit.outfitId, selectedColor.colorId)
                            }
                        }

                        dialog.dismiss()
                    }
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                },
                onAddClick = {
                    lifecycleScope.launch {
                        val dialogView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.dialog_select_color_attribute, null)
                        val dialogRecyclerView =
                            dialogView.findViewById<RecyclerView>(R.id.recycler_view_color_picker)

                        val allColors = sharedVM.getAllColors()

                        var dialog: AlertDialog? = null

                        val dialogAdapter = ColorAdapter(
                            emptyList(),
                            onColorClick = { selectedColor ->
                                lifecycleScope.launch {
                                    sharedVM.currentOutfit.value?.let { outfit ->
                                        sharedVM.addColorToOutfit(outfit.outfitId, selectedColor.colorId)
                                    }
                                }
                                dialog?.dismiss()
                            },
                            onAddClick = {
                                showColorPickerDialog()
                            },
                            colorBackground = "#000000"
                        )

                        dialogRecyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                            flexDirection = FlexDirection.ROW
                            flexWrap = FlexWrap.WRAP
                        }
                        dialogRecyclerView.adapter = dialogAdapter
                        allColors.observe(viewLifecycleOwner) { colors ->
                            dialogAdapter.updateColors(colors)
                        }

                        dialog = AlertDialog.Builder(requireContext())
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

            sharedVM.currentOutfit.observe(viewLifecycleOwner) { outfit ->
                outfit?.let {
                    sharedVM.getOutfitColors(it.outfitId).observe(viewLifecycleOwner) { colorList ->
                        adapter.updateColors(colorList)
                    }
                }
            }
        }

        // Set up attributes recycler view
        lifecycleScope.launch {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_attributes)
            val adapter = AttributeAdapter(
                emptyList(),
                onAttributeClick = { selectedAttribute ->
                    val dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_confirmation, null)
                    val dialogTitle = dialogView.findViewById<TextView>(R.id.question_text)
                    dialogTitle.text = getString(R.string.remove_attribute)
                    val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
                    val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .create()
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    acceptButton.setOnClickListener {
                        lifecycleScope.launch {
                            sharedVM.currentOutfit.value?.let { outfit ->
                                sharedVM.deleteAttributeFromOutfit(
                                    outfit.outfitId,
                                    selectedAttribute.attributeId
                                )
                            }
                        }

                        dialog.dismiss()
                    }
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                },
                onAddClick = {
                    lifecycleScope.launch {
                        val dialogView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.dialog_select_color_attribute, null)
                        val dialogRecyclerView =
                            dialogView.findViewById<RecyclerView>(R.id.recycler_view_color_picker)

                        val allAttributes = sharedVM.getAllAttributes()

                        var dialog: AlertDialog? = null

                        val dialogAdapter = AttributeAdapter(
                            emptyList(),
                            onAttributeClick = { selectedAttribute ->
                                lifecycleScope.launch {
                                    sharedVM.currentOutfit.value?.let { outfit ->
                                        sharedVM.addAttributeToOutfit(
                                            outfit.outfitId,
                                            selectedAttribute.attributeId
                                        )
                                    }
                                }
                                dialog?.dismiss()
                            },
                            onAddClick = {
                                showAddAttributeDialog()
                            },
                            colorBackground = "#000000"
                        )

                        allAttributes.observe(viewLifecycleOwner) { attributes ->
                            dialogAdapter.updateAttributeList(attributes)
                        }

                        dialogRecyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                            flexDirection = FlexDirection.ROW
                            flexWrap = FlexWrap.WRAP
                        }
                        dialogRecyclerView.adapter = dialogAdapter

                        dialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .create()

                        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog?.show()
                    }

                },
                colorBackground = "#3B3B3B"
            )

            recyclerView.adapter = adapter
            recyclerView.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            sharedVM.currentOutfit.observe(viewLifecycleOwner) { outfit ->
                outfit?.let {
                    sharedVM.getOutfitAttributes(it.outfitId).observe(viewLifecycleOwner) { attributeList ->
                        adapter.updateAttributeList(attributeList)
                    }
                }
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
                sharedVM.createNewAttribute(name)
                dialog.dismiss()
            } else {
                input.error = getString(R.string.error_attribute_empty)
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showColorPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_color_picker_custom, null)

        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)
        val colorPickerView = dialogView.findViewById<ColorPickerView>(R.id.colorPickerView)
        val brightnessSlide = dialogView.findViewById<BrightnessSlideBar>(R.id.brightnessSlide)

        // Delay attachment until views are laid out
        colorPickerView.post {
            colorPickerView.attachBrightnessSlider(brightnessSlide)
        }

        // Update color preview live
        colorPickerView.setColorListener(ColorListener { colorEnvelope, _ ->
            val drawable = colorPreview.background.mutate() as GradientDrawable
            drawable.setColor(colorEnvelope)
        })



        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val buttonAccept = dialogView.findViewById<Button>(R.id.buttonAccept)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)

        buttonAccept.setOnClickListener {
            val selectedColor = colorPickerView.color
            val hex = String.format("#%06X", (0xFFFFFF and selectedColor))
            lifecycleScope.launch {
                sharedVM.insertColor(
                    Color(
                    name = dialogView.findViewById<EditText>(R.id.colorName).text.toString()
                    , hexCode = hex)
                )
            }
            dialog.dismiss()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.box_bottom_navigation)
        dialog.show()
    }
}