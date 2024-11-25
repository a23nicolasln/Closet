package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.closet.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.adapters.ClothingAdapter
import com.example.closet.objects.ClothingItem
import androidx.recyclerview.widget.GridLayoutManager

class Outfits : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = false
        closetIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_closet)
        }
        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.isSelected = true

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvOutfits)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val clothingItems = listOf(
            ClothingItem(R.drawable.clothing_image_1),
            ClothingItem(R.drawable.clothing_image_2),
            ClothingItem(R.drawable.clothing_image_1),
            ClothingItem(R.drawable.clothing_image_2),
            ClothingItem(R.drawable.clothing_image_1),
            ClothingItem(R.drawable.clothing_image_2)
            // Add more items as needed
        )
        recyclerView.adapter = ClothingAdapter(clothingItems) {
            view.findNavController().navigate(R.id.action_outfits_to_outfitAdd)
        }

        return view
    }
}