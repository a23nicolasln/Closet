package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.ui.adapters.OutfitAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.data.model.Outfit
import com.example.closet.ui.adapters.OutfitWithProfilePictureAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AccountFragment : Fragment() {
    private var selectedImageUri: Uri? = null

    // Register image picker launcher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadProfilePicture(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Bottom navigation bar setup
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        val homeIcon = view.findViewById<ImageView>(R.id.home_icon)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        val accountIcon = view.findViewById<LinearLayout>(R.id.account_icon)
        val accountIconImage = view.findViewById<ShapeableImageView>(R.id.account_icon_image)

        accountIcon.isSelected = true
        closetIcon.isSelected = false
        homeIcon.isSelected = false
        searchIcon.isSelected = false

        closetIcon.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToLoadingFragment(true)
            findNavController().navigate(action)
        }
        homeIcon.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        searchIcon.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        // Outfit list display (only current user's published outfits)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_outfits)
        val outfitAdapter = OutfitWithProfilePictureAdapter(
            dataSet = emptyList(),
            onItemClick = {
                val action = AccountFragmentDirections.actionAccountFragmentToOutfitViewSocialFragment(it.outfitId, it.userId)
                findNavController().navigate(action)
            },
            showProfilePicture = false
        )
        recyclerView.adapter = outfitAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseSyncManager.getPublishedOutfitsByUserId(userId) { outfitList ->
                outfitAdapter.updateItems(outfitList)
            }
        }

        // Upload button navigation
        val uploadButton = view.findViewById<FloatingActionButton>(R.id.upload_button)
        uploadButton.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToUploadSelectorFragment()
            findNavController().navigate(action)
        }

        // Profile picture selection and upload
        val profilePicture = view.findViewById<ImageView>(R.id.profile_picture)
        profilePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Load profile picture from Firebase Realtime Database
        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUrl")
            databaseRef.get().addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_account)
                        .into(profilePicture)
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_account)
                        .centerCrop()
                        .into(accountIconImage)
                }
            }
        }

        // Load username from Firebase Realtime Database
        val usernameTextView = view.findViewById<TextView>(R.id.username)
        val usernameRef = FirebaseDatabase.getInstance().getReference("users/$userId/username")
        usernameRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.getValue(String::class.java)
            if (!username.isNullOrEmpty()) {
                usernameTextView.text = username
            }
        }

        // Load the user's followers and following count
        val followersCountTextView = view.findViewById<TextView>(R.id.num_followers)
        val followingCountTextView = view.findViewById<TextView>(R.id.num_following)

        if (userId != null) {
            FirebaseSyncManager.observeFollowerCount(userId) { count ->
                followersCountTextView.text = count.toString()
            }
        }

        if (userId != null) {
            FirebaseSyncManager.observeFollowingCount(userId) { count ->
                followingCountTextView.text = count.toString()
            }
        }


        return view
    }

    // Upload profile picture to Firebase Storage and save URL to Realtime Database
    private fun uploadProfilePicture(uri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "profile_picture_${UUID.randomUUID()}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference
            .child("user_profile_pictures/$userId/$fileName")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
                    databaseRef.child("profilePictureUrl").setValue(imageUrl)

                    Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                    view?.findViewById<ImageView>(R.id.profile_picture)?.setImageURI(uri)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }
}
