package com.example.closet.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.model.ClothingItem
import java.io.File

class ClothingItemAdapterSmall(
    private var dataSet: List<ClothingItem>,
    private val onItemClick: (ClothingItem) -> Unit
) : RecyclerView.Adapter<ClothingItemAdapterSmall.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycled_item_small, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val clothingItem = dataSet[position]
        val imageUrl = clothingItem.imageUrl
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

        viewHolder.imageView.setOnClickListener {
            onItemClick(clothingItem)
        }
    }


    override fun getItemCount() = dataSet.size

    fun updateItems(newItems: List<ClothingItem>) {
        // Sort by timestamp (newest first)
        dataSet = newItems.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

}
