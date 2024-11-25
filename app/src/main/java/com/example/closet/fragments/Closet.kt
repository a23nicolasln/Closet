package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

        return view
    }

}