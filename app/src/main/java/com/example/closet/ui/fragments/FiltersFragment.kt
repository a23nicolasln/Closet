package com.example.closet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.adapters.AttributeAdapter
import com.example.closet.ui.adapters.ColorAdapter
import com.example.closet.ui.viewmodels.ClosetViewModel
import com.example.closet.ui.viewmodels.FiltersViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class FiltersFragment : Fragment() {
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var attributeRepository: AttributeRepository
    private lateinit var colorRepository: ColorRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var viewModel: FiltersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clothingItemRepository = ClothingItemRepository(AppDatabase.getDatabase(requireContext()).clothingItemDao())
        typeRepository = TypeRepository(AppDatabase.getDatabase(requireContext()).typeDao())
        attributeRepository = AttributeRepository(AppDatabase.getDatabase(requireContext()).attributeDao())
        colorRepository = ColorRepository(AppDatabase.getDatabase(requireContext()).colorDao())
        outfitRepository = OutfitRepository(AppDatabase.getDatabase(requireContext()).outfitDao())
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
        viewModel = ViewModelProvider(this, viewModelFactory)[FiltersViewModel::class.java]



        // Retrieve RecyclerViews
        val recyclerColors = view.findViewById<RecyclerView>(R.id.recycler_view_colors)
        val recyclerTypes = view.findViewById<RecyclerView>(R.id.recycler_view_types)
        val recyclerAttributes = view.findViewById<RecyclerView>(R.id.recycler_view_attributes)

        // Disable type if its not an outfit else show all types
        val isOutfit = arguments?.getBoolean("isOutfit") ?: false
        val frameTypes = view.findViewById<View>(R.id.frame_layout_types)
        if (isOutfit) {
            recyclerTypes.visibility = View.GONE
            frameTypes.visibility = View.GONE
        }else {

        }

        // Back button
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            if (isOutfit) {
                val action = FiltersFragmentDirections.actionFiltersFragmentToOutfits()
                findNavController().navigate(action)
            } else {
                val action = FiltersFragmentDirections.actionFiltersFragmentToCloset()
                findNavController().navigate(action)
            }
        }

        // Set up RecyclerColors
        val colorsAdapter = ColorAdapter(
            colorList = emptyList(),
            onColorClick = {},
            onAddClick = {},
            colorBackground = "#3B3B3B",
        )
        recyclerColors.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerColors.adapter = colorsAdapter
        viewModel.colors.observe(viewLifecycleOwner) { colors ->
            colorsAdapter.updateColors(colors)
        }

        // Set up RecyclerAttributes
        val attributesAdapter = AttributeAdapter(
            attributeList = emptyList(),
            onAttributeClick = {},
            onAddClick = {},
            colorBackground = "#3B3B3B",
        )
        recyclerAttributes.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerAttributes.adapter = attributesAdapter
        viewModel.attributes.observe(viewLifecycleOwner) { attributes ->
            attributesAdapter.updateAttributeList(attributes)
        }

        return view
    }
}