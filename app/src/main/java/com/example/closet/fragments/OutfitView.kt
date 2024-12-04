package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.adapters.ClothingItemAdapter
import com.example.closet.dao.DaoOutfit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import java.io.File

class OutfitView : Fragment() {
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
        val id = arguments?.getString("id")
        val daoOutfit = DaoOutfit(requireContext())
        val outfit = daoOutfit.getOutfitById(id!!)

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
        if (outfit != null) {
            recyclerViewItems.adapter = ClothingItemAdapter(outfit.clothingItems) { clothingItem ->
                findNavController().navigate(OutfitViewDirections.actionOutfitViewToClothingView(clothingItem.id))
            }
        }

        //Delete button
        val deleteButton = view.findViewById<TextView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            daoOutfit.deleteOutfit(outfit!!)
            findNavController().navigateUp()
        }

        return view
    }
}