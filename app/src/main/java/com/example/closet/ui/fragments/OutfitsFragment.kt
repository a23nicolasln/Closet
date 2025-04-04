package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.closet.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.closet.ui.adapters.OutfitAdapter
import com.example.closet.ui.viewmodels.OutfitsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OutfitsFragment : Fragment() {

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

        //RecyclerView for outfits
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvOutfits)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[OutfitsViewModel::class.java]

        recyclerView.adapter = OutfitAdapter(viewModel.getAll())

        return view
    }
}