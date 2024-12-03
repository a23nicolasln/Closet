package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.closet.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.closet.adapters.OutfitAdapter
import com.example.closet.dao.DaoOutfit
import com.example.closet.objects.Outfit
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Outfits : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        //Navigation for account button
        val accountButton = view.findViewById<FloatingActionButton>(R.id.acount_button)
        accountButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_account)
        }

        //Navigation for closet button
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = false
        closetIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_closet)
        }

        //Background color change for closet button
        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.isSelected = true

        //Add Outfit
        val addOutfit = Outfit(id = "add", name = "Add Outfit", clothingItems = listOf(), imageUrl = "")

        //RecyclerView for outfits
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvOutfits)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val daoOutfit = DaoOutfit(requireContext())
        recyclerView.adapter = OutfitAdapter(daoOutfit.getAllOutfits() + addOutfit)



        return view
    }
}