package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.MyApp
import com.example.closet.R
import com.example.closet.ui.adapters.OutfitAdapter
import com.example.closet.ui.viewmodels.OutfitsViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class OutfitsFragment : Fragment() {

    private lateinit var viewModel: OutfitsViewModel
    private lateinit var adapter: OutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        // Set up navigation for account button
        val accountButton = view.findViewById<FloatingActionButton>(R.id.settings_button)
        accountButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }

        // Filters button
        val filtersButton = view.findViewById<FloatingActionButton>(R.id.filter_button)
        filtersButton.setOnClickListener {
            val action = OutfitsFragmentDirections.actionOutfitsToFiltersFragment(true)
            view.findNavController().navigate(action)
        }

        // Set up navigation for closet button
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = false
        closetIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_closet)
        }
        // Set up navigation for social button
        val socialIcon = view.findViewById<ImageView>(R.id.social_icon)
        socialIcon.isSelected = false
        socialIcon.setOnClickListener {
            view.findNavController().navigate(OutfitsFragmentDirections.actionOutfitsToLoadingFragment(false))
        }

        // Background color change for outfits button
        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.isSelected = true

        // Initialize ViewModel using custom factory
        val application = requireActivity().application as MyApp
        val factory = ViewModelFactory { OutfitsViewModel(application.outfitRepository) }
        viewModel = ViewModelProvider(this, factory)[OutfitsViewModel::class.java]

        // Set up RecyclerView for outfits
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvOutfits)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Initialize the adapter with an empty list or initial data
        adapter = OutfitAdapter(
            emptyList(),
            onItemClick = { outfit ->
                val action = OutfitsFragmentDirections.actionOutfitsToOutfitAdd(outfit.outfitId)
                view.findNavController().navigate(action)
            }
        )
        recyclerView.adapter = adapter

        // Observe LiveData and update the adapter
        viewModel.getAll().observe(viewLifecycleOwner) { outfits ->
            adapter.updateItems(outfits)  // Update the adapter with new data
        }

        // Set up navigation for adding a new outfit
        val addOutfitButton = view.findViewById<FloatingActionButton>(R.id.add_button)
        addOutfitButton.setOnClickListener {
            val action = OutfitsFragmentDirections.actionOutfitsToOutfitAdd(0L)
            view.findNavController().navigate(action)
        }

        return view
    }
}
