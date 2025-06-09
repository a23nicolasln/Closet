package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.dto.ClothingItemDTO

class ClothingItemDTOAdapterSmall(
    private var dataSet: List<ClothingItemDTO>,
    private val onItemClick: (ClothingItemDTO) -> Unit
) : RecyclerView.Adapter<ClothingItemDTOAdapterSmall.ViewHolder>() {

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
        val imageUrl = clothingItem.imgUrl
        val context = viewHolder.imageView.context

        if (imageUrl.isEmpty()) {
            viewHolder.imageView.setImageResource(R.drawable.placeholder_image)
        } else {
            Glide.with(context)
                .load(imageUrl) // Load from Firebase URL
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(viewHolder.imageView)
        }

        viewHolder.imageView.setOnClickListener {
            onItemClick(clothingItem)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateItems(newItems: List<ClothingItemDTO>) {
        dataSet = newItems
        notifyDataSetChanged()
    }
}
