package com.example.closet.ui.fragments

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

class ClosetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_closet, container, false)

        //Account, Outfits and Closet icons
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
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Jacket")
            view.findNavController().navigate(action)
        }
        closetBoxTshirts.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("T-shirt")
            view.findNavController().navigate(action)
        }
        closetBoxJumpers.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Jumper")
            view.findNavController().navigate(action)
        }
        closetBoxTrousers.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Trousers")
            view.findNavController().navigate(action)
        }
        closetBoxShoes.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Shoes")
            view.findNavController().navigate(action)
        }

        val jewelryBoxNecklaces = view.findViewById<CardView>(R.id.jewelryBoxNecklaces)
        val jewelryBoxEarrings = view.findViewById<CardView>(R.id.jewelryBoxEarrings)
        val jewelryBoxBracelets = view.findViewById<CardView>(R.id.jewelryBoxBracelets)
        val jewelryBoxRings = view.findViewById<CardView>(R.id.jewelryBoxRings)

        jewelryBoxNecklaces.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Necklace")
            view.findNavController().navigate(action)
        }

        jewelryBoxEarrings.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Earring")
            view.findNavController().navigate(action)
        }
        jewelryBoxBracelets.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Bracelet")
            view.findNavController().navigate(action)
        }

        jewelryBoxRings.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector("Ring")
            view.findNavController().navigate(action)
        }


        return view
    }

}