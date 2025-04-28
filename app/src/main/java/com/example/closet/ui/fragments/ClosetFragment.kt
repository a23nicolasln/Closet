package com.example.closet.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.viewmodels.ClosetViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.closet.ui.adapters.TypeAdapter
import com.example.closet.ui.viewmodels.ViewModelFactory

class ClosetFragment : Fragment() {
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var viewModel: ClosetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clothingItemRepository = ClothingItemRepository(AppDatabase.getDatabase(requireContext()).clothingItemDao())
        typeRepository = TypeRepository(AppDatabase.getDatabase(requireContext()).typeDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_closet, container, false)

        // Initialize ViewModel
        val viewModelFactory = ViewModelFactory {
            ClosetViewModel(
                clothingItemRepository = clothingItemRepository,
                typeRepository = typeRepository
            )
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[ClosetViewModel::class.java]

        // Filters button
        val filtersButton = view.findViewById<FloatingActionButton>(R.id.filter_button)
        filtersButton.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToFiltersFragment(false)
            view.findNavController().navigate(action)
        }

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.typesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create an empty adapter initially
        val typeAdapter = TypeAdapter(emptyList(), emptyMap(), ::onTypeClick, ::onClothingItemClick)
        recyclerView.adapter = typeAdapter

        // Observe changes in types and clothing items
        viewModel.allTypes.observe(viewLifecycleOwner) { types ->
            val groupedItems = viewModel.clothingItemsByType.value ?: emptyMap()
            typeAdapter.updateItems(types, groupedItems)
        }

        viewModel.clothingItemsByType.observe(viewLifecycleOwner) { groupedItems ->
            val types = viewModel.allTypes.value ?: emptyList()
            typeAdapter.updateItems(types, groupedItems)
        }

        // Icons navigation
        val accountIcon = view.findViewById<FloatingActionButton>(R.id.account_button)
        accountIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_closet_to_account)
        }

        val outfitsIcon = view.findViewById<ImageView>(R.id.outfits_icon)
        outfitsIcon.setOnClickListener {
            view.findNavController().navigate(R.id.action_closet_to_outfits)
        }

        val closetIcon = view.findViewById<ImageView>(R.id.closet_icon)
        closetIcon.isSelected = true

        // Add new type button
        val addTypeButton = view.findViewById<FloatingActionButton>(R.id.add_button)
        addTypeButton.setOnClickListener {
            showAddTypeDialog()
        }

        return view
    }

    // Handle type click event
    private fun onTypeClick(type: Type) {
        val action = ClosetFragmentDirections.actionClosetToClothingSelector(type.typeId)
        view?.findNavController()?.navigate(action)
    }

    // Handle clothing item click event
    private fun onClothingItemClick(clothingItem: ClothingItem) {
        val action = ClosetFragmentDirections.actionClosetToClothingAdd(clothingItem.typeOwnerId,clothingItem.clothingItemId)
        view?.findNavController()?.navigate(action)
    }

    private fun showAddTypeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_type, null)
        val input = dialogView.findViewById<EditText>(R.id.typeNameInput)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSave.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                // Save the type (via ViewModel)
                viewModel.insertType(Type(0, name)) // Replace with actual type logic
                dialog.dismiss()
            } else {
                input.error = "Type name can't be empty"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
