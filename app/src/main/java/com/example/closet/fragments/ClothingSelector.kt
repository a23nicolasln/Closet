package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.closet.R

class ClothingSelector : Fragment() {

    private val args: ClothingSelectorArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clothing_selector, container, false)

        //Back button
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Get the type of clothing item
        val type = args.clothingType

        return view
    }
}