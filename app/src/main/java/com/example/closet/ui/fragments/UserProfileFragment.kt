package com.example.closet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.ui.adapters.OutfitWithProfilePictureAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserProfileFragment : Fragment() {

    // Get the user ID from the arguments
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Back button setup
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        val profilePicture = view.findViewById<ShapeableImageView>(R.id.profile_picture)
        val userName = view.findViewById<TextView>(R.id.username)

        // Load profile picture and username from Firebase Realtime Database
        if (userId != null) {
            val databaseRef =
                FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUrl")
            databaseRef.get().addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_account)
                        .into(profilePicture)
                }
            }

            val usernameRef = FirebaseDatabase.getInstance().getReference("users/$userId/username")
            usernameRef.get().addOnSuccessListener { snapshot ->
                val username = snapshot.getValue(String::class.java)
                if (!username.isNullOrEmpty()) {
                    userName.text = username
                }
            }
        }

        // Load the uploaded outfits for the user
        FirebaseSyncManager.getPublishedOutfitsByUserId(userId) { outfitList ->
            val outfitAdapter = OutfitWithProfilePictureAdapter(
                dataSet = outfitList,
                onItemClick = { outfit ->
                    val action =
                        UserProfileFragmentDirections.actionUserProfileFragmentToOutfitViewSocialFragment(
                            outfit.outfitId,
                            outfit.userId
                        )
                    findNavController().navigate(action)
                },
                showProfilePicture = false
            )
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_outfits)
            recyclerView.adapter = outfitAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        // Load the user's followers and following count
        val followersCountTextView = view.findViewById<TextView>(R.id.num_followers)
        val followingCountTextView = view.findViewById<TextView>(R.id.num_following)

        FirebaseSyncManager.observeFollowerCount(userId) { count ->
            followersCountTextView.text = count.toString()
        }

        FirebaseSyncManager.observeFollowingCount(userId) { count ->
            followingCountTextView.text = count.toString()
        }


        // Follow/Unfollow button setup
        val followButton = view.findViewById<Button>(R.id.follow_button)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?:
            throw IllegalStateException("Current user ID is null")

        var isFollowing = false

        FirebaseSyncManager.isUserFollowing(userId, currentUserId) { following ->
            isFollowing = following
            followButton.text = if (isFollowing) "Unfollow" else "Follow"
        }

        followButton.setOnClickListener {
            if (isFollowing) {
                FirebaseSyncManager.unfollowUser(userId, currentUserId)
                followButton.text = "Follow"
            } else {
                FirebaseSyncManager.followUser(userId, currentUserId)
                followButton.text = "Unfollow"
            }
            isFollowing = !isFollowing
        }


        return view
    }
}