package com.example.closet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.ui.adapters.OutfitAdapter
import com.example.closet.ui.adapters.OutfitWithProfilePictureAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Navigation bar setup (unchanged)
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        val homeIcon = view.findViewById<ImageView>(R.id.home_icon)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        val accountIcon = view.findViewById<LinearLayout>(R.id.account_icon)
        val accountIconImage = view.findViewById<ShapeableImageView>(R.id.account_icon_image)

        homeIcon.isSelected = true
        closetIcon.isSelected = false
        searchIcon.isSelected = false
        accountIcon.isSelected = false

        closetIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToLoadingFragment(true)
            view.findNavController().navigate(action)
        }
        searchIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            view.findNavController().navigate(action)
        }
        accountIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAccountFragment()
            view.findNavController().navigate(action)
        }

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val outfitAdapter = OutfitWithProfilePictureAdapter(
            dataSet = emptyList(),
            onItemClick = {
                val outfitId = it.outfitId
                val action = HomeFragmentDirections.actionHomeFragmentToOutfitViewSocialFragment(
                    outfitId = outfitId,
                    userId = it.userId
                )
                view.findNavController().navigate(action)
            },
            showProfilePicture = true
        )
        recyclerView.adapter = outfitAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)

        FirebaseSyncManager.getAllPublishedOutfits { outfitList ->
            outfitAdapter.updateItems(outfitList)
        }

        // Load profile picture from Firebase DB
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUrl")
            databaseRef.get().addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_account)
                        .centerCrop()
                        .into(accountIconImage)
                }
            }
        }

        return view
    }
}