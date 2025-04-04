package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.OutfitViewViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class OutfitViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_outfit_view, container, false)

        // Navigation for back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        //Get the object from the id
        val id = arguments?.getLong("id")
        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[OutfitViewViewModel::class.java]
        val outfit = viewModel.getById(id ?: 0)

        // Write name of outfit to screen
        val text = view.findViewById<TextView>(R.id.outfitName)
        if (outfit != null) {
            text.text = outfit.name
        }

        //Replace image with outfit image

        if (outfit != null) {
            Glide.with(requireContext())
                .load(File(outfit.imageUrl))
                .into(view.findViewById(R.id.imageViewOutfit))
        }

        //Recycle view for the outfit
        val recyclerViewItems = view.findViewById<RecyclerView>(R.id.recyclerViewItems)
        recyclerViewItems.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        /*if (outfit != null) {
            recyclerViewItems.adapter = ClothingItemAdapter(outfit.clothingItems) { clothingItem ->
                findNavController().navigate(OutfitViewFragmentDirections.actionOutfitViewToClothingView(clothingItem.clothingItemId))
            }
        }*/

        //Delete button
        val deleteButton = view.findViewById<TextView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            viewModel.delete(outfit)
            findNavController().navigateUp()
        }

        return view
    }
}