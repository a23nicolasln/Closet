package com.example.closet.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.ui.adapters.OutfitWithProfilePictureAdapter
import com.example.closet.ui.adapters.UserSearchAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var outfitAdapter: OutfitWithProfilePictureAdapter
    private lateinit var userAdapter: UserSearchAdapter
    private var showingUsers = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Bottom nav setup
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        val homeIcon = view.findViewById<ImageView>(R.id.home_icon)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        val accountIcon = view.findViewById<LinearLayout>(R.id.account_icon)
        val accountIconImage = view.findViewById<ShapeableImageView>(R.id.account_icon_image)

        searchIcon.isSelected = true

        closetIcon.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToLoadingFragment(true)
            view.findNavController().navigate(action)
        }

        homeIcon.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToHomeFragment()
            view.findNavController().navigate(action)
        }

        accountIcon.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToAccountFragment()
            view.findNavController().navigate(action)
        }

        // Load profile picture
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUrl")
            databaseRef.get().addOnSuccessListener { snapshot ->
                snapshot.getValue(String::class.java)?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_account)
                        .centerCrop()
                        .into(accountIconImage)
                }
            }
        }

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // initial: show outfits
        outfitAdapter = OutfitWithProfilePictureAdapter(
            dataSet = emptyList(),
            onItemClick = {
                val action = SearchFragmentDirections.actionSearchFragmentToOutfitViewSocialFragment(
                    outfitId = it.outfitId,
                    userId = it.userId,
                )
                view.findNavController().navigate(action)
            },
            showProfilePicture = true
        )

        userAdapter = UserSearchAdapter(
            emptyList(),
            onUserClick = {
                val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(it.userId)
                view.findNavController().navigate(action)
            }
        )

        recyclerView.adapter = outfitAdapter

        // Load initial outfits
        FirebaseSyncManager.getAllPublishedOutfits { outfitList ->
            outfitAdapter.updateItems(outfitList)
        }

        // Search input logic
        searchInput = view.findViewById(R.id.search_edit_text)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isNotEmpty()) {
                    if (!showingUsers) {
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.adapter = userAdapter
                        showingUsers = true
                    }

                    FirebaseSyncManager.getUsersByUsernamePrefix(query) { users ->
                        userAdapter.updateUsers(users)
                    }
                } else {
                    if (showingUsers) {
                        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                        recyclerView.adapter = outfitAdapter
                        showingUsers = false
                    }

                    FirebaseSyncManager.getAllPublishedOutfits { outfitList ->
                        outfitAdapter.updateItems(outfitList)
                    }
                }
            }
        })

        return view
    }
}
