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
import com.example.closet.repository.OutfitRepository
import com.example.closet.ui.adapters.OutfitAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Bottom navigation bar
        // Retrieve all navigation buttons
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        val homeIcon = view.findViewById<ImageView>(R.id.home_icon)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        val accountIcon = view.findViewById<ImageView>(R.id.account_icon)
        // Set the selected state for the search icon
        accountIcon.isSelected = true
        // Set the unselected state for the other icons
        closetIcon.isSelected = false
        homeIcon.isSelected = false
        searchIcon.isSelected = false
        // Set up navigation for closet button
        closetIcon.setOnClickListener {
            // Navigate to the closet fragment
            val action = AccountFragmentDirections.actionAccountFragmentToLoadingFragment(true)
            findNavController().navigate(action)
        }
        // Set up navigation for home button
        homeIcon.setOnClickListener {
            // Navigate to the home fragment
            val action = AccountFragmentDirections.actionAccountFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        // Set up navigation for search button
        searchIcon.setOnClickListener {
            // Navigate to the search fragment
            val action = AccountFragmentDirections.actionAccountFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        //Get the recycled view
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_outfits)
        // as a test get all outfits from the repository directly and set it to the adapter
        val outfitRepository = OutfitRepository(AppDatabase.getDatabase(requireContext()).outfitDao())
        val outfitAdapter = OutfitAdapter(
            dataSet = emptyList(),
            onItemClick = {}
        )
        recyclerView.adapter = outfitAdapter
        // Set the layout manager for the RecyclerView with agrid layout
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        lifecycleScope.launch {
            val outfits = outfitRepository.getAllOutfits()
            outfits.observe(viewLifecycleOwner) { outfitList ->
                outfitAdapter.updateItems(outfitList)
            }
        }




        return view
    }
}