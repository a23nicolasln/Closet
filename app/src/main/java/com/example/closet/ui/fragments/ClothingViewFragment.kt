package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.ClothingViewViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ClothingViewFragment : Fragment() {

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
        val clothingItemId = arguments?.getLong("id")

        // Get the clothing item from the database
        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[ClothingViewViewModel::class.java]
        val clothingItem = viewModel.getById(clothingItemId!!)

        if (clothingItem != null) {
            view.findViewById<TextView>(R.id.colors).text = clothingItem.color
            view.findViewById<TextView>(R.id.brand).text = clothingItem.brand
            view.findViewById<TextView>(R.id.size).text = clothingItem.size
            Glide.with(requireContext())
                .load(File(clothingItem.imageUrl))
                .into(view.findViewById(R.id.imageViewClothing))
        }

        val deleteButton = view.findViewById<TextView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            viewModel.delete(clothingItem)
            findNavController().navigateUp()
        }

        return view
    }
}