package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.closet.R
import com.example.closet.dao.DaoClothingItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class ClothingView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clothing_view, container, false)

        // Set up the back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Show the clothing item details as a json string
        val clothingItemId = arguments?.getString("id")
        DaoClothingItem(requireContext()).getClothingItemById(clothingItemId!!)?.let { clothingItem ->
            val clothingItemJson = Gson().toJson(clothingItem)
            view.findViewById<TextView>(R.id.textViewClothingJson).text = clothingItemJson
        }

        return view
    }
}