package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.model.Type
import com.example.closet.ui.fragments.ClosetFragmentDirections

class TypeAdapter(private var items: List<Type>) :
    RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeNameTextView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycled_item_type, parent, false)
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val type = items[position]
        holder.typeNameTextView.text = type.name
        holder.itemView.setOnClickListener {
            val action = ClosetFragmentDirections.actionClosetToClothingSelector(type.typeId)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Type>) {
        items = newItems
        notifyDataSetChanged()
    }
}
