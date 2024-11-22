package com.example.closet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController

class Closet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothes_jewerly_type_selector, container, false)

        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.isSelected = false
        outfitsIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_clothesJewerlyTypeSelector_to_outfits)
        }

        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = true

        return view
    }

}