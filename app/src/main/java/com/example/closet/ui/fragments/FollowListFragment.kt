package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.ui.adapters.UserSearchAdapter


class FollowListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_follow_list, container, false)

        val userId = arguments?.getString("userId") ?: return view
        val isFollowers = arguments?.getBoolean("isFollowers") ?: return view

        val recyclerView = view.findViewById<RecyclerView>(R.id.follow_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = UserSearchAdapter(
            userList = emptyList(),
            onUserClick = {
                val action = FollowListFragmentDirections
                    .actionFollowListFragmentToUserProfileFragment(it.userId)
                view.findNavController().navigate(action)
            }
        )
        recyclerView.adapter = adapter

        if (isFollowers) {
            FirebaseSyncManager.getFollowersForUser(userId) { followers ->
                adapter.updateUsers(followers)
            }
        } else {
            FirebaseSyncManager.getFollowingForUser(userId) { following ->
                adapter.updateUsers(following)
            }
        }

        // Back button setup
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}
