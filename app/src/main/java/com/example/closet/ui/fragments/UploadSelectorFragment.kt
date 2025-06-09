package com.example.closet.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.dto.ClothingItemDTO
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.data.model.Outfit
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.ui.adapters.OutfitAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class UploadSelectorFragment : Fragment() {

    private lateinit var outfitRepository: OutfitRepository
    private lateinit var crossRefRepository: OutfitClothingItemRepository
    private lateinit var outfitAdapter: OutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_upload_selector, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        outfitRepository = OutfitRepository(AppDatabase.getDatabase(requireContext()).outfitDao())
        crossRefRepository = OutfitClothingItemRepository(AppDatabase.getDatabase(requireContext()).outfitClothingItemDao())
        outfitAdapter = OutfitAdapter(
            dataSet = emptyList(),
            onItemClick = { outfit ->
                // Handle outfit click
                uploadOutfit(outfit)
            }
        )

        recyclerView.adapter = outfitAdapter

        // Observe outfit list
        val outfits = outfitRepository.getAllOutfits()
        outfits.observe(viewLifecycleOwner) { outfitList ->
            outfitAdapter.updateItems(outfitList)
        }

        // Back button
        val backButton = view.findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun uploadOutfit(outfit: Outfit) {
        lifecycleScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val clothingItems = outfitRepository.getClothingItemsForOutfit(outfit.outfitId)

                val outfitDTO = OutfitDTO(
                    outfitId = outfit.outfitId,
                    name = outfit.name,
                    imageUrl = outfit.imageUrl,
                    userId = userId,
                    clothingItems = clothingItems.map {
                        ClothingItemDTO(
                            clothingItemId = it.clothingItemId,
                            imgUrl = it.imageUrl,
                            link = it.link
                        )
                    }
                )

                // Start progress UI (show progress bar or dialog)
                //showProgress(true)

                // Upload the outfit
                val finalOutfit = FirebaseSyncManager.prepareOutfitWithUploadedImages(outfitDTO, userId)
                FirebaseSyncManager.uploadOutfitToRealtimeDatabase(finalOutfit, userId)

                // Check if fragment is still active before updating UI
                if (isAdded) {
                    Toast.makeText(requireContext(), "Outfit uploaded!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UploadError", "Failed to upload outfit", e)

                // Ensure the fragment is still active before showing a Toast
                if (isAdded) {
                    Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                // Hide progress UI when done
                //showProgress(false)
            }
        }
    }

   /* private fun showProgress(show: Boolean) {
        // Here, you can show or hide a ProgressBar or ProgressDialog
        val progressBar = view?.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }*/
}

