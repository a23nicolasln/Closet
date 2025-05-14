package com.example.closet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.ui.adapters.ClothingItemDTOAdapterSmall
import kotlin.properties.Delegates

class OutfitViewSocialFragment : Fragment() {

    private var outfitId by Delegates.notNull<Long>()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            outfitId = it.getLong("outfitId", 0L)
            userId = it.getString("userId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_outfit_view_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val recyclerClothingItems = view.findViewById<RecyclerView>(R.id.recycler_view_clothing_items)
        recyclerClothingItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val clothingItemAdapter = ClothingItemDTOAdapterSmall(
            dataSet = emptyList(),
            onItemClick = {
                // Optional: handle click
            }
        )
        recyclerClothingItems.adapter = clothingItemAdapter

        val outfitImage = view.findViewById<ImageView>(R.id.imageViewOutfit)
        val outfitName = view.findViewById<TextView>(R.id.outfitName)

        FirebaseSyncManager.getOutfitById(outfitId, userId) { outfit ->
            if (outfit != null) {
                outfitName.text = outfit.name

                Glide.with(requireContext())
                    .load(outfit.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(outfitImage)

                clothingItemAdapter.updateItems(outfit.clothingItems)
            } else {
                outfitName.text = ""
                clothingItemAdapter.updateItems(emptyList())
            }
        }
    }
}
