package com.example.closet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.adapters.ClothingItemAdapter
import com.example.closet.dao.DaoClothingItem
import com.example.closet.objects.ClothingItem
import java.nio.file.Paths

class ClothingSelector : Fragment() {

    private val args: ClothingSelectorArgs by navArgs()

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
        val clothingItems: MutableList<ClothingItem> = DaoClothingItem(requireContext()).getClothingByType(type).toMutableList()

        // Add a dummy item for the "Add Item" card
        clothingItems.add(ClothingItem(id = "add", type = type, brand = "", color = listOf(), size = "", imageUrl = ""))
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = ClothingItemAdapter(clothingItems) { clothingItem ->
            if (clothingItem.id == "add") {
                val action = ClothingSelectorDirections.actionClothingSelectorToClothingAdd(clothingItem.type)
                view.findNavController().navigate(action)
            } else {
                val action = ClothingSelectorDirections.actionClothingSelectorToClothingView(clothingItem.id)
                view.findNavController().navigate(action)
            }
        }

        return view
    }
}