package com.example.closet.ui.fragments

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
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
import com.example.closet.utils.FileUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UploadSelectorFragment : Fragment() {

    private lateinit var outfitRepository: OutfitRepository
    private lateinit var crossRefRepository: OutfitClothingItemRepository
    private lateinit var outfitAdapter: OutfitAdapter
    private lateinit var appContext: android.content.Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_upload_selector, container, false)
        appContext = requireContext().applicationContext

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        outfitRepository = OutfitRepository(AppDatabase.getDatabase(requireContext()).outfitDao())
        crossRefRepository = OutfitClothingItemRepository(AppDatabase.getDatabase(requireContext()).outfitClothingItemDao())
        outfitAdapter = OutfitAdapter(
            dataSet = emptyList(),
            onItemClick = { outfit ->
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_confirmation, null)
                val dialogQuestion = dialogView.findViewById<TextView>(R.id.question_text)
                dialogQuestion.text = getString(R.string.upload_outfit, outfit.name)
                val acceptButton = dialogView.findViewById<Button>(R.id.accept_button)
                val cancelButton = dialogView.findViewById<View>(R.id.cancel_button)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                acceptButton.setOnClickListener {
                    dialog.dismiss()
                    findNavController().navigateUp()
                    uploadOutfitInBackground(outfit)
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

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

    private fun uploadOutfitInBackground(outfit: Outfit) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val builder = NotificationCompat.Builder(appContext, "upload_channel")
                .setSmallIcon(R.drawable.icon_clear) // replace with your icon
                .setContentTitle("Uploading outfit")
                .setContentText("In progress...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 0, true) // Indeterminate progress

            notificationManager.notify(1, builder.build())

            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val clothingItems = outfitRepository.getClothingItemsForOutfit(outfit.outfitId)

                val outfitDTO = OutfitDTO(
                    outfitId = outfit.outfitId,
                    name = outfit.name,
                    imageUrl = outfit.imageUrl,
                    userId = userId,
                    clothingItems = clothingItems.map {
                        ClothingItemDTO(it.clothingItemId, it.imageUrl, it.link)
                    }
                )

                val finalOutfit = FirebaseSyncManager.prepareOutfitWithUploadedImages(outfitDTO, userId)
                FirebaseSyncManager.uploadOutfitToRealtimeDatabase(finalOutfit, userId)

                // Update to success
                builder.setContentText("Upload complete")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(1, builder.build())

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(appContext, "Outfit uploaded!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("UploadError", "Upload failed", e)

                // Update to error
                builder.setContentText("Upload failed")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(1, builder.build())

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(appContext, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }





    /* private fun showProgress(show: Boolean) {
         // Here, you can show or hide a ProgressBar or ProgressDialog
         val progressBar = view?.findViewById<ProgressBar>(R.id.progress_bar)
         progressBar?.visibility = if (show) View.VISIBLE else View.GONE
     }*/
}

