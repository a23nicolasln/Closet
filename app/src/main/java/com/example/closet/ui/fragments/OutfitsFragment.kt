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

class OutfitsFragment : Fragment() {

    private lateinit var viewModel: OutfitsViewModel
    private lateinit var adapter: OutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        // Set up navigation for account button
        val accountButton = view.findViewById<FloatingActionButton>(R.id.acount_button)
        accountButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_account)
        }

        // Set up navigation for closet button
        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = false
        closetIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_outfits_to_closet)
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
        adapter = OutfitAdapter(emptyList())  // Empty list initially
        recyclerView.adapter = adapter

        // Observe LiveData and update the adapter
        viewModel.getAll().observe(viewLifecycleOwner) { outfits ->
            adapter.updateItems(outfits)  // Update the adapter with new data
        }

        return view
    }
}
