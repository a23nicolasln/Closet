package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.repository.AttributeRepository
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.ColorRepository
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.data.repository.TypeRepository
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory

class OutfitAddClothingSelectorFragment : Fragment() {

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
    private lateinit var adapter: ClothingItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_add_clothing_selector, container, false)

        // Get clothing type from arguments
        val clothingType = arguments?.getLong("clothingType") ?: 0L

        setupRecyclerView(view, clothingType)
        setupBackButton(view)

        return view
    }

    private fun setupRecyclerView(view: View, clothingType: Long) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ClothingItemAdapter(emptyList()) { clothingItem ->
            // Add selected item to shared ViewModel and navigate back
            sharedVM.addClothingItem(clothingItem)
            sharedVM.saveOutfit()
            val action =
                OutfitAddClothingSelectorFragmentDirections.actionOutfitAddClothingSelectorToOutfitAdd(sharedVM.currentOutfit.value?.outfitId ?: 0L)
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        // Observe clothing items by type from shared ViewModel
        sharedVM.getClothingItemsByType(clothingType).observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }
    }

    private fun setupBackButton(view: View) {
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}