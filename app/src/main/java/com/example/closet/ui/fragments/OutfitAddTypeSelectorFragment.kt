package com.example.closet.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type
import com.example.closet.repository.AttributeRepository
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.ColorRepository
import com.example.closet.repository.OutfitClothingItemRepository
import com.example.closet.repository.OutfitRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.adapters.TypeAdapter
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OutfitAddTypeSelectorFragment : Fragment() {

    // Shared ViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_add_type_selector, container, false)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.typesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create an empty adapter initially
        val typeAdapter = TypeAdapter(emptyList(), emptyMap(), ::onTypeClick, ::onClothingItemClick)
        recyclerView.adapter = typeAdapter

        // Observe changes in types and clothing items
        sharedVM.allTypes.observe(viewLifecycleOwner) { types ->
            val groupedItems = sharedVM.clothingItemsByType.value ?: emptyMap()
            typeAdapter.updateItems(types, groupedItems)
        }

        sharedVM.clothingItemsByType.observe(viewLifecycleOwner) { groupedItems ->
            val types = sharedVM.allTypes.value ?: emptyList()
            typeAdapter.updateItems(types, groupedItems)
        }

        //Set up back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            requireView().findNavController().popBackStack()
        }

        return view
    }

    // Handle type click event
    private fun onTypeClick(type: Type) {
        val action = OutfitAddTypeSelectorFragmentDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector(type.typeId)
        view?.findNavController()?.navigate(action)
    }

    // Handle clothing item click event
    private fun onClothingItemClick(clothingItem: ClothingItem) {

        sharedVM.addClothingItem(clothingItem)
        sharedVM.saveOutfit()
        val action =
            OutfitAddTypeSelectorFragmentDirections.actionOutfitAddTypeSelectorToOutfitAdd(sharedVM.currentOutfit.value?.outfitId ?: 0L)
        view?.findNavController()?.navigate(action)
    }

}
