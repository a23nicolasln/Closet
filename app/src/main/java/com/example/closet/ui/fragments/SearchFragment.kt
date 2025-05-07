package com.example.closet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.closet.R

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Bottom navigation bar
        // Retrieve all navigation buttons
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        val homeIcon = view.findViewById<ImageView>(R.id.home_icon)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        val accountIcon = view.findViewById<ImageView>(R.id.account_icon)
        // Set the selected state for the search icon
        searchIcon.isSelected = true
        // Set the unselected state for the other icons
        closetIcon.isSelected = false
        homeIcon.isSelected = false
        accountIcon.isSelected = false
        // Set up navigation for closet button
        closetIcon.setOnClickListener {
            // Navigate to the closet fragment
            val action = SearchFragmentDirections.actionSearchFragmentToLoadingFragment(true)
            view.findNavController().navigate(action)
        }
        // Set up navigation for home button
        homeIcon.setOnClickListener {
            // Navigate to the home fragment
            val action = SearchFragmentDirections.actionSearchFragmentToHomeFragment()
            view.findNavController().navigate(action)
        }
        // Set up navigation for account button
        accountIcon.setOnClickListener {
            // Navigate to the account fragment
            val action = SearchFragmentDirections.actionSearchFragmentToAccountFragment()
            view.findNavController().navigate(action)
        }



        return view
    }
}