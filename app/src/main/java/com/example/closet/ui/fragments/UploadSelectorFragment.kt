package com.example.closet.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.firebase.FirebaseSyncManager.publishOutfit
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.ui.adapters.OutfitAdapter
import com.example.closet.utils.FileUtils.uploadImageToFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UploadSelectorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_upload_selector, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        // Set up the RecyclerView with a Grid layout
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Get the DAO and set up adapter
        val outfitRepository = AppDatabase.getDatabase(requireContext()).outfitDao()
        val crossRefRepository = AppDatabase.getDatabase(requireContext()).outfitClothingItemDao()
        val outfitAdapter = OutfitAdapter(
            dataSet = emptyList(),
            onItemClick = { outfit ->
                val imageUriString = outfit.imageUrl
                if (imageUriString.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No image URI", Toast.LENGTH_SHORT).show()
                    return@OutfitAdapter
                }

                try {
                    val imageUri = Uri.parse(imageUriString)

                    lifecycleScope.launch {
                        val downloadUrl = uploadImageToFirebase(requireContext(), imageUri.path ?: "")
                        if (downloadUrl != null) {
                            val clothingItemIds = outfitRepository.getClothingItemsForOutfit(outfit.outfitId)

                            val outfitDTO = OutfitDTO(
                                outfitId = outfit.outfitId,
                                name = outfit.name,
                                imageUrl = downloadUrl,
                                clothingItems = clothingItemIds,
                                userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            )
                            publishOutfit(outfitDTO)

                            Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
                        }

                        findNavController().navigate(
                            UploadSelectorFragmentDirections.actionUploadSelectorFragmentToAccountFragment()
                        )
                    }

                } catch (e: Exception) {
                    Log.e("Upload", "Invalid URI: $imageUriString", e)
                    Toast.makeText(requireContext(), "Invalid image URI", Toast.LENGTH_SHORT).show()
                }
            }
        )





        recyclerView.adapter = outfitAdapter

        // Observe outfit list
        val outfits = outfitRepository.getAllOutfits()
        outfits.observe(viewLifecycleOwner) { outfitList ->
            outfitAdapter.updateItems(outfitList)
        }

        return view
    }
}
