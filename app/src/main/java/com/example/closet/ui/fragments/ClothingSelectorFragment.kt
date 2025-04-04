package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.ClothingSelectorViewModel

class ClothingSelectorFragment : Fragment() {

    private val args: ClothingSelectorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothing_selector, container, false)

        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val type = args.clothingType
        val factory = ViewModelProvider.NewInstanceFactory()
        val viewModel = ViewModelProvider(this, factory)[ClothingSelectorViewModel::class.java]
        val clothingItems = viewModel.getByType(type)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = ClothingItemAdapter(clothingItems) { clothingItem ->
            if (clothingItem.clothingItemId == 1L) {
                val action = ClothingSelectorFragmentDirections.actionClothingSelectorToClothingAdd(clothingItem.type)
                view.findNavController().navigate(action)
            } else {
                val action = ClothingSelectorFragmentDirections.actionClothingSelectorToClothingView(clothingItem.clothingItemId)
                view.findNavController().navigate(action)
            }
        }

        return view
    }
}