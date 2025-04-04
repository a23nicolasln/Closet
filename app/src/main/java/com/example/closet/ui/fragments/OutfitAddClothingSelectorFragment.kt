package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.OutfitAddClothingSelectorViewModel

class OutfitAddClothingSelectorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothing_selector, container, false)

        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Get the clothing type from the arguments
        val type = arguments?.getString("clothingType")

        // Get the clothing items of the specified type from the database
        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[OutfitAddClothingSelectorViewModel::class.java]
        val clothingItems = viewModel.getByType(type ?: "")

        // RecyclerView for clothing items
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = clothingItems?.let {
            ClothingItemAdapter(it) { clothingItem ->
                val action =
                    OutfitAddClothingSelectorFragmentDirections.actionOutfitAddClothingSelectorToOutfitAdd(clothingItem.clothingItemId)
                view.findNavController().navigate(action)

            }
        }

        return view
    }
}