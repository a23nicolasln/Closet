package com.example.closet.ui.fragments

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.Attribute
import com.example.closet.data.model.Color
import com.example.closet.data.model.Type
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.adapters.AttributeAdapter
import com.example.closet.ui.adapters.ColorAdapter
import com.example.closet.ui.adapters.TypeSmallAdapter
import com.example.closet.ui.viewmodels.FiltersViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import kotlinx.coroutines.launch


class FiltersFragment : Fragment() {
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var attributeRepository: AttributeRepository
    private lateinit var colorRepository: ColorRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var viewModel: FiltersViewModel
    private var isOutfit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clothingItemRepository = ClothingItemRepository(AppDatabase.getDatabase(requireContext()).clothingItemDao())
        typeRepository = TypeRepository(AppDatabase.getDatabase(requireContext()).typeDao())
        attributeRepository = AttributeRepository(AppDatabase.getDatabase(requireContext()).attributeDao())
        colorRepository = ColorRepository(AppDatabase.getDatabase(requireContext()).colorDao())
        outfitRepository = OutfitRepository(AppDatabase.getDatabase(requireContext()).outfitDao())
        isOutfit = arguments?.getBoolean("isOutfit") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filters, container, false)

        // Initialize ViewModel
        val viewModelFactory = ViewModelFactory {
            FiltersViewModel(
                clothingItemRepository = clothingItemRepository,
                typeRepository = typeRepository,
                attributeRepository = attributeRepository,
                colorRepository = colorRepository,
                outfitRepository = outfitRepository
            )
        }
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.nav_graph)
        viewModel = ViewModelProvider(navBackStackEntry, viewModelFactory)[FiltersViewModel::class.java]




        // Retrieve RecyclerViews
        val recyclerColors = view.findViewById<RecyclerView>(R.id.recycler_view_colors)
        val recyclerTypes = view.findViewById<RecyclerView>(R.id.recycler_view_types)
        val recyclerAttributes = view.findViewById<RecyclerView>(R.id.recycler_view_attributes)

        // Disable type if its not an outfit else show all types
        val frameTypes = view.findViewById<View>(R.id.frame_layout_types)

        if (isOutfit) {
            frameTypes.visibility = View.GONE
            recyclerTypes.visibility = View.GONE
        } else {
            recyclerTypes.visibility = View.VISIBLE
            frameTypes.visibility = View.VISIBLE
        }

        // Back button
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            if (isOutfit) {
                viewModel.clearSelection()
                val action = FiltersFragmentDirections.actionFiltersFragmentToOutfits()
                findNavController().navigate(action)
            } else {
                viewModel.clearSelection()
                val action = FiltersFragmentDirections.actionFiltersFragmentToCloset()
                findNavController().navigate(action)
            }
        }

        // Search button
        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            val action = FiltersFragmentDirections.actionFiltersFragmentToFilteredFragment(isOutfit)
            findNavController().navigate(action)
        }

        // Menu button
        val menuButton = view.findViewById<FloatingActionButton>(R.id.menu_button)
        menuButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.menu_filters, popupMenu.menu)

            // Change text based on `isOutfit
            val filterItem = popupMenu.menu.findItem(R.id.action_filter_by_outfits)
            filterItem.title = if (isOutfit) getString(R.string.filter_items) else getString(R.string.filter_outfits)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_filter_by_outfits -> {
                        if (isOutfit){
                            isOutfit = false
                            enableType()
                        }else{
                            isOutfit = true
                            disableType()
                            viewModel.clearTypeSelection()
                            recyclerTypes.adapter?.notifyDataSetChanged()
                        }
                        true
                    }
                    R.id.action_clear_filters -> {
                        viewModel.clearSelection()
                        recyclerTypes.adapter?.notifyDataSetChanged()
                        recyclerAttributes.adapter?.notifyDataSetChanged()
                        recyclerColors.adapter?.notifyDataSetChanged()
                        true
                    }
                    R.id.action_delete_selected -> {
                        deletionConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)

            popupMenu.show()
        }

        // Set up RecyclerColors
        val colorsAdapter = ColorAdapter(
            colorList = emptyList(),
            onColorClick = {viewModel.selectColor(it)},
            onAddClick = { showColorPickerDialog() },
            colorBackground = "#3B3B3B",
        )
        recyclerColors.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerColors.adapter = colorsAdapter
        viewModel.colors.observe(viewLifecycleOwner) { colors ->
            val selected = viewModel.getSelectedColors()
            colorsAdapter.updateColors(colors, selected)
        }

        // Set up RecyclerAttributes
        val attributesAdapter = AttributeAdapter(
            attributeList = emptyList(),
            onAttributeClick = {viewModel.selectAttribute(it)},
            onAddClick = {showAddAttributeDialog()},
            colorBackground = "#3B3B3B",
        )
        recyclerAttributes.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerAttributes.adapter = attributesAdapter
        viewModel.attributes.observe(viewLifecycleOwner) { attributes ->
            val selected = viewModel.getSelectedAttributes()
            attributesAdapter.updateAttributeList(attributes, selected)
        }

        // Set up RecyclerTypes
        val typesAdapter = TypeSmallAdapter(
            typeList = emptyList(),
            onTypeClick = {viewModel.selectType(it)},
            onAddClick = {showAddTypeDialog()},
            colorBackground = "#3B3B3B"
        )
        recyclerTypes.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerTypes.adapter = typesAdapter
        viewModel.types.observe(viewLifecycleOwner) { types ->
            val selected = viewModel.getSelectedTypes()
            typesAdapter.updateTypeList(types, selected)
        }


        return view
    }

    private fun showAddTypeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_type, null)
        val input = dialogView.findViewById<EditText>(R.id.typeNameInput)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSave.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.insertType(Type(0, name))
                dialog.dismiss()
            } else {
                input.error = getString(R.string.error_type_empty)
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
                viewModel.insertAttribute(Attribute(0, name))
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
                viewModel.insertColor(Color(
                    name = dialogView.findViewById<EditText>(R.id.colorName).text.toString()
                    , hexCode = hex))
            }
            dialog.dismiss()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.box_bottom_navigation)
        dialog.show()
    }






    private fun disableType() {
        val recyclerTypes = view?.findViewById<RecyclerView>(R.id.recycler_view_types)
        recyclerTypes?.visibility = View.GONE
        val frameTypes = view?.findViewById<View>(R.id.frame_layout_types)
        frameTypes?.visibility = View.GONE
    }

    private fun enableType() {
        val recyclerTypes = view?.findViewById<RecyclerView>(R.id.recycler_view_types)
        recyclerTypes?.visibility = View.VISIBLE
        val frameTypes = view?.findViewById<View>(R.id.frame_layout_types)
        frameTypes?.visibility = View.VISIBLE
    }

    private fun deletionConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_confirmation, null)
        val dialogQuestion = dialogView.findViewById<TextView>(R.id.question_text)
        dialogQuestion.text = getString(R.string.delete_filters)
        val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
        val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        acceptButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteSelected()
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}