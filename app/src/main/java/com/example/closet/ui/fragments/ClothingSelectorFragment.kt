package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.ui.adapters.ClothingItemAdapter
import com.example.closet.ui.viewmodels.ClothingSelectorViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.example.closet.MyApp
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.ClothingViewViewModel

class ClothingSelectorFragment : Fragment() {

    private val args: ClothingSelectorFragmentArgs by navArgs()
    private lateinit var viewModel: ClothingSelectorViewModel
    private lateinit var adapter: ClothingItemAdapter
    private lateinit var repository: ClothingItemRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_clothing_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access repository from Application class (assuming MyApp provides the repository)
        val application = requireActivity().application as MyApp
        repository = application.clothingItemRepository

        // Initialize ViewModel with the factory
        val viewModelFactory = ViewModelFactory(
            creator = { ClothingSelectorViewModel(repository) }
        )
        val viewModel = ViewModelProvider(this, viewModelFactory)[ClothingSelectorViewModel::class.java]
        // Set up RecyclerView
        adapter = ClothingItemAdapter(emptyList()) { clothingItem ->
            val action = ClothingSelectorFragmentDirections.actionClothingSelectorToClothingView(clothingItem.clothingItemId)
            view.findNavController().navigate(action)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvClothingSelector)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        // Observe filtered clothing items
        viewModel.getByType(args.clothingType)?.observe(viewLifecycleOwner) { clothingItems ->
            adapter.updateItems(clothingItems)
        }

        // Set up click listener for the "Add" button
        view.findViewById<View>(R.id.add_button).setOnClickListener {
            val action = ClothingSelectorFragmentDirections.actionClothingSelectorToClothingAdd(args.clothingType)
            view.findNavController().navigate(action)
        }

        // Back button
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}
