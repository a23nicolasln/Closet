package com.example.closet.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.model.Outfit
import com.example.closet.ui.fragments.OutfitsFragmentDirections
import java.io.File

class OutfitAdapter(
    private var dataSet: List<Outfit>,
    private val onItemClick: (Outfit) -> Unit // Listener for item click
) : RecyclerView.Adapter<OutfitAdapter.ViewHolder>() {

    // Provide a reference to the type of views that you are using (custom ViewHolder)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycled_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val imageUrl = dataSet[position].imageUrl
        val context = viewHolder.imageView.context

        val imageFile = if (imageUrl.isNotEmpty()) File(imageUrl) else null
        val shouldLoadPlaceholder = imageUrl.isEmpty() || imageFile == null || !imageFile.exists()

        if (shouldLoadPlaceholder) {
            viewHolder.imageView.setImageResource(R.drawable.placeholder_image) // Your placeholder
        } else {
            Glide.with(context)
                .load(imageFile)
                .placeholder(R.drawable.placeholder_image)
                .into(viewHolder.imageView)

        }

        // Set the click listener
        viewHolder.imageView.setOnClickListener {
            onItemClick(dataSet[position])
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun updateItems(newItems: List<Outfit>) {
        // Sort by timestamp (newest first)
        dataSet = newItems.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

}
