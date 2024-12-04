package com.example.closet.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.fragments.OutfitsDirections
import com.example.closet.fragments.Outfits
import com.example.closet.objects.ClothingItem
import com.example.closet.objects.Outfit
import java.io.File

class OutfitAdapter(private val dataSet: List<Outfit>) :
    RecyclerView.Adapter<OutfitAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            // Define click listener for the ViewHolder's View
            imageView = view.findViewById(R.id.item_image)
        }
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
        if (dataSet[position].id == "add") {
            viewHolder.imageView.setImageResource(R.drawable.icon_plus)
            viewHolder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

            viewHolder.imageView.setOnClickListener {
                val action =
                    OutfitsDirections.actionOutfitsToOutfitAdd("")
                viewHolder.imageView.findNavController().navigate(action)
            }

        } else {

            val imageUrl = dataSet[position].imageUrl
            Log.d("ClothingItemAdapter", "Loading image URL: $imageUrl")

            Glide.with(viewHolder.imageView.context)
                .load(File(imageUrl))
                .into(viewHolder.imageView)


            viewHolder.imageView.setOnClickListener {
                val action =
                    OutfitsDirections.actionOutfitsToOutfitView(dataSet[position].id)
                viewHolder.imageView.findNavController().navigate(action)
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}