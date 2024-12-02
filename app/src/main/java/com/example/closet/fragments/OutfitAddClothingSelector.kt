package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.adapters.ClothingItemAdapter
import com.example.closet.dao.DaoClothingItem
import com.example.closet.objects.ClothingItem

class OutfitAddClothingSelector : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothing_selector, container, false)

        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val type = arguments?.getString("clothingType")
        val clothingItems: MutableList<ClothingItem>? =
            type?.let { DaoClothingItem(requireContext()).getClothingByType(it).toMutableList() }

        // RecyclerView for clothing items
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = clothingItems?.let {
            ClothingItemAdapter(it) { clothingItem ->
                val action =
                    OutfitAddClothingSelectorDirections.actionOutfitAddClothingSelectorToOutfitAdd(clothingItem.id)
                view.findNavController().navigate(action)

            }
        }

        return view
    }
}