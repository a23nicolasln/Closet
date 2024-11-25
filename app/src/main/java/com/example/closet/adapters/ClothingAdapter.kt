package com.example.closet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.objects.ClothingItem

class ClothingAdapter(
    private val clothingItems: List<ClothingItem>,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == clothingItems.size) VIEW_TYPE_ADD else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.clothing_item_add, parent, false)
            AddViewHolder(view, onAddClick)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.clothing_item, parent, false)
            ClothingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ClothingViewHolder) {
            val clothingItem = clothingItems[position]
            Glide.with(holder.itemView.context)
                .load(clothingItem.imageUrl)
                .into(holder.clothingImage)
        }
    }

    override fun getItemCount() = clothingItems.size + 1

    class ClothingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clothingImage: ImageView = itemView.findViewById(R.id.clothing_item_image)
    }

    class AddViewHolder(itemView: View, onAddClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onAddClick()
            }
        }
    }
}