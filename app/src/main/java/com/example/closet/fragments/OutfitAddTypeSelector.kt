package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.closet.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OutfitAddTypeSelector : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_add_type_selector, container, false)

        //Navigation for back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            view.findNavController().navigate(OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAdd(""))
        }

        //Navigation and transfer of type of clothing item

        val closetBoxJackets = view.findViewById<CardView>(R.id.closetBoxJackets)
        val closetBoxTshirts = view.findViewById<CardView>(R.id.closetBoxTshirts)
        val closetBoxJumpers = view.findViewById<CardView>(R.id.closetBoxJumpers)
        val closetBoxTrousers = view.findViewById<CardView>(R.id.closetBoxTrousers)
        val closetBoxShoes = view.findViewById<CardView>(R.id.closetBoxShoes)

        closetBoxJackets.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Jacket")
            view.findNavController().navigate(action)
        }
        closetBoxTshirts.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("T-shirt")
            view.findNavController().navigate(action)
        }
        closetBoxJumpers.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Jumper")
            view.findNavController().navigate(action)
        }
        closetBoxTrousers.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Trousers")
            view.findNavController().navigate(action)
        }
        closetBoxShoes.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Shoes")
            view.findNavController().navigate(action)
        }

        val jewelryBoxNecklaces = view.findViewById<CardView>(R.id.jewelryBoxNecklaces)
        val jewelryBoxEarrings = view.findViewById<CardView>(R.id.jewelryBoxEarrings)
        val jewelryBoxBracelets = view.findViewById<CardView>(R.id.jewelryBoxBracelets)
        val jewelryBoxRings = view.findViewById<CardView>(R.id.jewelryBoxRings)

        jewelryBoxNecklaces.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Necklace")
            view.findNavController().navigate(action)
        }

        jewelryBoxEarrings.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Earring")
            view.findNavController().navigate(action)
        }
        jewelryBoxBracelets.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Bracelet")
            view.findNavController().navigate(action)
        }

        jewelryBoxRings.setOnClickListener {
            val action = OutfitAddTypeSelectorDirections.actionOutfitAddTypeSelectorToOutfitAddClothingSelector("Ring")
            view.findNavController().navigate(action)
        }


        return view
    }
}