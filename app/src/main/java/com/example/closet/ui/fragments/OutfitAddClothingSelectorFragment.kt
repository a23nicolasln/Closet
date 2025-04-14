package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
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
                joinRepo = OutfitClothingItemRepository(database.outfitClothingItemDao())
            )
        }
    }
    private lateinit var adapter: ClothingItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothing_selector, container, false)

        // Get clothing type from arguments
        val clothingType = arguments?.getString("clothingType") ?: ""

        setupRecyclerView(view, clothingType)
        setupBackButton(view)

        return view
    }

    private fun setupRecyclerView(view: View, clothingType: String) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ClothingItemAdapter(emptyList()) { clothingItem ->
            // Add selected item to shared ViewModel and navigate back
            sharedVM.addClothingItem(clothingItem)
            findNavController().navigateUp()
        }
        recyclerView.adapter = adapter

        // Observe clothing items by type from shared ViewModel
        sharedVM.getClothingItemsByType(clothingType)?.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }
    }

    private fun setupBackButton(view: View) {
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}