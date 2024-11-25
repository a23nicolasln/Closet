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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Outfits : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        val accountButton = view.findViewById<FloatingActionButton>(R.id.acount_button)
        accountButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_account)
        }

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
            ClothingItem("1", "type1", "color1", "size1", "url1"),
            ClothingItem("2", "type2", "color2", "size2", "url2"),
            ClothingItem("3", "type3", "color3", "size3", "url3"),
            ClothingItem("4", "type4", "color4", "size4", "url4"),
            ClothingItem("5", "type5", "color5", "size5", "url5"),
            ClothingItem("6", "type6", "color6", "size6", "url6")
            // Add more items as needed
        )
        recyclerView.adapter = ClothingAdapter(clothingItems) {
            view.findNavController().navigate(R.id.action_outfits_to_outfitAdd)
        }

        return view
    }
}