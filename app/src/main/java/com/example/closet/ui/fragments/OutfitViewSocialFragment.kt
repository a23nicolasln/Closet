package com.example.closet.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.FirebaseSyncManager.getUsersProfilePicture
import com.example.closet.data.firebase.FirebaseSyncManager.isLikedOutfit
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
        val outfit = null

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
        //val outfitName = view.findViewById<TextView>(R.id.outfitName)

        FirebaseSyncManager.getOutfitById(outfitId, userId) { outfit ->
            if (outfit != null) {
                Glide.with(requireContext())
                    .load(outfit.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(outfitImage)

                clothingItemAdapter.updateItems(outfit.clothingItems)
            } else {
                clothingItemAdapter.updateItems(emptyList())
            }

        }


        // Retrive social iteraction buttons
        val likeButton = view.findViewById<ImageView>(R.id.like_button)
        val commentButton = view.findViewById<ImageView>(R.id.comment_button)
        val likecount = view.findViewById<TextView>(R.id.like_count)
        val accountButton = view.findViewById<ImageView>(R.id.account_button)



        FirebaseSyncManager.observeLikeCount(userId, outfitId) { likeCount ->
            likecount.text = likeCount.toString()
        }

        getUsersProfilePicture(userId) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_account)
                    .into(accountButton)
            }
        }

        isLikedOutfit(userId, outfitId) { isLiked ->
            if (isLiked) {
                likeButton.setImageResource(R.drawable.icon_liked)
                likeButton.setPadding(0, 10, 0, 10)
            } else {
                likeButton.setImageResource(R.drawable.icon_like)
            }
        }

        likeButton.setOnClickListener {
            Log.d("OutfitViewSocialFragment", "Like button clicked")
            FirebaseSyncManager.likeOutfit(userId,outfitId) { isLiked ->
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.icon_liked)
                    likeButton.setPadding(0, 10, 0, 10)
                } else {
                    likeButton.setImageResource(R.drawable.icon_like)
                }
            }
        }

        commentButton.setOnClickListener {
            // Handle comment button click
        }

        accountButton.setOnClickListener {
            val action = outfitId.let {
                OutfitViewSocialFragmentDirections.actionOutfitViewSocialFragmentToUserProfileFragment(
                    userId
                )
            }
            findNavController().navigate(action)
        }
    }
}
