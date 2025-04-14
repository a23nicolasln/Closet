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

class OutfitAdapter(private var dataSet: List<Outfit>) :
    RecyclerView.Adapter<OutfitAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycled_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val imageUrl = dataSet[position].imageUrl
        Log.d("ClothingItemAdapter", "Loading image URL: $imageUrl")

        Glide.with(viewHolder.imageView.context)
            .load(File(imageUrl))
            .into(viewHolder.imageView)

        viewHolder.imageView.setOnClickListener {
            val action =
                OutfitsFragmentDirections.actionOutfitsToOutfitAdd(0, dataSet[position].outfitId)
            viewHolder.imageView.findNavController().navigate(action)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    // 🆕 Method to update the list dynamically
    fun updateItems(newItems: List<Outfit>) {
        dataSet = newItems
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }
}
