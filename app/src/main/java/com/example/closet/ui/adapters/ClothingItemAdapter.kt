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

class ClothingItemAdapter(
    private var dataSet: List<ClothingItem>,
    private val onItemClick: (ClothingItem) -> Unit
) : RecyclerView.Adapter<ClothingItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycled_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val clothingItem = dataSet[position]

        if (clothingItem.clothingItemId == 1L) {
            viewHolder.imageView.setImageResource(R.drawable.icon_plus)
            viewHolder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        } else {
            val imageUrl = clothingItem.imageUrl
            Log.d("ClothingItemAdapter", "Loading image URL: $imageUrl")
            Glide.with(viewHolder.imageView.context)
                .load(File(imageUrl))
                .into(viewHolder.imageView)
        }

        viewHolder.imageView.setOnClickListener {
            onItemClick(clothingItem)
        }
    }

    override fun getItemCount() = dataSet.size

    // Method to update the list dynamically
    fun updateItems(newItems: List<ClothingItem>) {
        dataSet = newItems
        notifyDataSetChanged()
    }
}
