package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.model.Attribute

class AttributeAdapter(
    private val attributeList: List<Attribute>,
    private val onAttributeClick: (Attribute) -> Unit,  // Handler for attribute clicks
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ATTRIBUTE = 0
    private val TYPE_ADD_BUTTON = 1

    inner class AttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val attributeText: TextView = itemView.findViewById(R.id.attribute_text)

        init {
            itemView.setOnClickListener {
                val attribute = attributeList[adapterPosition]
                onAttributeClick(attribute) // Trigger the attribute click handler
            }
        }
    }

    inner class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<View>(R.id.add_button).setOnClickListener {
                onAddClick() // Trigger the add button click handler
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ATTRIBUTE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycled_item_attribute, parent, false)
                AttributeViewHolder(view)
            }
            TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycled_item_add, parent, false)
                AddButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AttributeViewHolder -> {
                val attribute = attributeList[position]
                holder.attributeText.text = attribute.name
            }
            is AddButtonViewHolder -> {
                // Add button view does not need to bind any data
            }
        }
    }

    override fun getItemCount(): Int = attributeList.size + 1 // +1 for the add button

    override fun getItemViewType(position: Int): Int {
        return if (position < attributeList.size) {
            TYPE_ATTRIBUTE
        } else {
            TYPE_ADD_BUTTON
        }
    }
}
