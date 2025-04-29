package com.example.closet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.data.database.AppDatabase
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.adapters.OutfitAdapter
import com.example.closet.ui.fragments.FiltersFragmentDirections
import com.example.closet.ui.viewmodels.FiltersViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory

class FilteredFragment : Fragment() {
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var attributeRepository: AttributeRepository
    private lateinit var colorRepository: ColorRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var viewModel: FiltersViewModel
    private var isOutfit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isOutfit = it.getBoolean("isOutfit", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_filtered, container, false)

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

        // Back button
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            val action = FilteredFragmentDirections.actionFilteredFragmentToFiltersFragment(isOutfit)
            findNavController().navigate(action)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_filtered)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        if (isOutfit) {
            val outfitAdapter = OutfitAdapter(
                dataSet = emptyList(),
                /*onItemClick = {
                    val action = FilteredFragmentDirections.actionFilteredFragmentToOutfitAdd(
                        outfitId = it.outfitId,
                    )
                    findNavController().navigate(action)
                }*/
            )
            recyclerView.adapter = outfitAdapter

            viewModel.filteredOutfits.observe(viewLifecycleOwner) { outfits ->
                outfitAdapter.updateItems(outfits)
            }

            viewModel.applyOutfitFilters()
        } else {
            val clothingItemAdapter = ClothingItemAdapter(
                dataSet = emptyList(),
                onItemClick = {
                    val action = FilteredFragmentDirections.actionFilteredFragmentToClothingAdd(
                        clothingType = it.typeOwnerId,
                        clothingId = it.clothingItemId,
                    )
                    findNavController().navigate(action)
                }
            )
            recyclerView.adapter = clothingItemAdapter

            viewModel.filteredClothingItems.observe(viewLifecycleOwner) { filteredItems ->
                clothingItemAdapter.updateItems(filteredItems)
            }

            viewModel.applyClothingItemFilters()
        }

        return view
    }


}