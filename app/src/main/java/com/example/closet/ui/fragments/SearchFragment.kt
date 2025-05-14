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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.ui.adapters.UserSearchAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var userAdapter: UserSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Bottom Nav setup (unchanged)
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

        // RecyclerView & Adapter
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserSearchAdapter(
            emptyList(),
            onUserClick = {
                val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(it.userId)
                view.findNavController().navigate(action)
            }
        )
        recyclerView.adapter = userAdapter

        // Search Input
        searchInput = view.findViewById<EditText>(R.id.search_edit_text)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    FirebaseSyncManager.getUsersByUsernamePrefix(query) { users ->
                        userAdapter.updateUsers(users)
                    }
                } else {
                    userAdapter.updateUsers(emptyList())
                }
            }
        })

        return view
    }
}
