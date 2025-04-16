package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Type
import com.example.closet.ui.fragments.ClosetFragment
import com.example.closet.ui.fragments.ClosetFragmentDirections
import com.example.closet.ui.viewmodels.ClosetViewModel
import com.example.closet.ui.viewmodels.ClothingSelectorViewModel

class TypeAdapter(
    private var types: List<Type>,
    private var clothingItemsByType: Map<Long, List<ClothingItem>>, // typeId -> list of clothing items
    private val onTypeClick: (Type) -> Unit,
    private val onClothingItemClick: (ClothingItem) -> Unit
) : RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeNameTextView: TextView = itemView.findViewById(R.id.textView)
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view_items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycled_item_type, parent, false)
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val type = types[position]
        holder.typeNameTextView.text = type.name

        holder.itemView.setOnClickListener {
            onTypeClick(type)
        }

        val clothingItems = clothingItemsByType[type.typeId].orEmpty()

        val clothingItemAdapter = ClothingItemAdapterSmall(clothingItems) { clothingItem ->
            onClothingItemClick(clothingItem)
        }

        holder.innerRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.innerRecyclerView.adapter = clothingItemAdapter
    }

    override fun getItemCount(): Int = types.size

    fun updateItems(newTypes: List<Type>, newClothingItemsByType: Map<Long, List<ClothingItem>>) {
        types = newTypes
        clothingItemsByType = newClothingItemsByType
        notifyDataSetChanged()
    }
}

