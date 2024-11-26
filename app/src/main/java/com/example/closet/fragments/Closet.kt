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

class Closet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_closet, container, false)


        val accountIcon = view.findViewById<FloatingActionButton>(R.id.account_button)
        accountIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_closet_to_account)
        }

        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.isSelected = false
        outfitsIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_closet_to_outfits)
        }

        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = true

        //Navigation and transfer of type of clothing item

        val closetBoxJackets = view.findViewById<CardView>(R.id.closetBoxJackets)
        val closetBoxTshirts = view.findViewById<CardView>(R.id.closetBoxTshirts)
        val closetBoxJumpers = view.findViewById<CardView>(R.id.closetBoxJumpers)
        val closetBoxTrousers = view.findViewById<CardView>(R.id.closetBoxTrousers)
        val closetBoxShoes = view.findViewById<CardView>(R.id.closetBoxShoes)

        closetBoxJackets.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Jackets")
            view.findNavController().navigate(action)
        }
        closetBoxTshirts.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("T-shirts")
            view.findNavController().navigate(action)
        }
        closetBoxJumpers.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Jumpers")
            view.findNavController().navigate(action)
        }
        closetBoxTrousers.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Trousers")
            view.findNavController().navigate(action)
        }
        closetBoxShoes.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Shoes")
            view.findNavController().navigate(action)
        }

        val jewelryBoxNecklaces = view.findViewById<CardView>(R.id.jewelryBoxNecklaces)
        val jewelryBoxEarrings = view.findViewById<CardView>(R.id.jewelryBoxEarrings)
        val jewelryBoxBracelets = view.findViewById<CardView>(R.id.jewelryBoxBracelets)
        val jewelryBoxRings = view.findViewById<CardView>(R.id.jewelryBoxRings)

        jewelryBoxNecklaces.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Necklaces")
            view.findNavController().navigate(action)
        }

        jewelryBoxEarrings.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Earrings")
            view.findNavController().navigate(action)
        }
        jewelryBoxBracelets.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Bracelets")
            view.findNavController().navigate(action)
        }

        jewelryBoxRings.setOnClickListener {
            val action = ClosetDirections.actionClosetToClothingSelector("Rings")
            view.findNavController().navigate(action)
        }


        return view
    }

}