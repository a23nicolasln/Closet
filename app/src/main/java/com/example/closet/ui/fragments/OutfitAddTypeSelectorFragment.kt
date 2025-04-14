package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OutfitAddTypeSelectorFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_add_type_selector, container, false)

        // Navigation for back button - no need to pass IDs
        view.findViewById<FloatingActionButton>(R.id.back_button).setOnClickListener {
            view.findNavController().navigateUp()
        }

        // Setup all category buttons
        setupCategoryButtons(view)

        return view
    }

    private fun setupCategoryButtons(view: View) {
        // Map of button IDs to clothing types
        val categoryButtons = mapOf(
            R.id.closetBoxJackets to "Jacket",
            R.id.closetBoxTshirts to "T-shirt",
            R.id.closetBoxJumpers to "Jumper",
            R.id.closetBoxTrousers to "Trousers",
            R.id.closetBoxShoes to "Shoes",
            R.id.jewelryBoxNecklaces to "Necklace",
            R.id.jewelryBoxEarrings to "Earring",
            R.id.jewelryBoxBracelets to "Bracelet",
            R.id.jewelryBoxRings to "Ring"
        )

        // Set up all buttons with a loop
        categoryButtons.forEach { (buttonId, clothingType) ->
            view.findViewById<CardView>(buttonId).setOnClickListener {
                navigateToClothingSelector(clothingType)
            }
        }
    }

    private fun navigateToClothingSelector(clothingType: String) {
        // No need to pass outfitId - it's in the shared ViewModel
        val action = OutfitAddTypeSelectorFragmentDirections
            .actionOutfitAddTypeSelectorToOutfitAddClothingSelector(clothingType)
        requireView().findNavController().navigate(action)
    }
}