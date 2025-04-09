package com.example.closet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.closet.MyApp
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.ClothingViewViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File

class ClothingViewFragment : Fragment() {

    private lateinit var clothingItem: ClothingItem
    private lateinit var repository: ClothingItemRepository
    var clothingItemId: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            clothingItemId = it.getLong("id")
        }

        // Initialize the repository here
        repository = ClothingItemRepository(
            AppDatabase.getDatabase(requireContext()).clothingItemDao()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clothing_view, container, false)

        // Set up the back button
        val backButton = view.findViewById<FloatingActionButton>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Using the ViewModelFactory to create the ViewModel with the repository
        val viewModelFactory = ViewModelFactory(
            creator = { ClothingViewViewModel(repository) }
        )
        val viewModel = ViewModelProvider(this, viewModelFactory)[ClothingViewViewModel::class.java]

        // Observe the clothing item
        viewModel.clothingItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                updateUI(view, it)
            }
        }

        // Load the clothing item
        clothingItemId?.let { id ->
            lifecycleScope.launch {
                viewModel.loadClothingItem(id)
            }
        }

        // Set up delete button
        view.findViewById<TextView>(R.id.delete_button).setOnClickListener {
            viewModel.clothingItem.value?.let { item ->
                lifecycleScope.launch {
                    viewModel.delete(item)
                    findNavController().navigateUp()
                }
            }
        }

        return view
    }

    private fun updateUI(view: View, clothingItem: ClothingItem) {
        view.findViewById<TextView>(R.id.colors).text = clothingItem.color
        view.findViewById<TextView>(R.id.brand).text = clothingItem.brand
        view.findViewById<TextView>(R.id.size).text = clothingItem.size
        Glide.with(requireContext())
            .load(File(clothingItem.imageUrl))
            .into(view.findViewById(R.id.imageViewClothing))
    }
}