package com.example.closet.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.closet.MyRecyclerViewAdapter
import com.example.closet.R


class Outfits : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)



        // set up the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.rvOutfits)
        val numberOfColumns = 6
        recyclerView.setLayoutManager(GridLayoutManager(, numberOfColumns))
        adapter = MyRecyclerViewAdapter(this, data)
        adapter.setClickListener(this)
        recyclerView.setAdapter(adapter)



        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = false
        closetIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_clothesJewerlyTypeSelector)
        }

        return view
    }

}