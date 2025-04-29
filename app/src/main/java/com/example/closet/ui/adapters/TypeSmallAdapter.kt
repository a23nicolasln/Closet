package com.example.closet.ui.adapters

import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.model.Type

class TypeSmallAdapter(
    private var typeList: List<Type>,
    private val onTypeClick: (Type) -> Unit,
    private val onAddClick: () -> Unit,
    private val colorBackground: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE = 0
    private val ADD_BUTTON = 1

    private var selectedType: Type? = null
    private var selectedTypes: List<Type> = emptyList()

    inner class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeText: TextView = itemView.findViewById(R.id.attribute_text)
        val colorBackground: ConstraintLayout = itemView.findViewById(R.id.color_background)

        fun bind(type: Type) {
            typeText.text = type.name

            itemView.setOnClickListener {
                selectedType = type
                notifyDataSetChanged()
                onTypeClick(type)
            }
        }
    }

    inner class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<View>(R.id.add_button).setOnClickListener {
                onAddClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycled_item_attribute, parent, false)
                TypeViewHolder(view)
            }
            ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycled_item_add, parent, false)
                AddButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TypeViewHolder -> {
                val type = typeList[position]
                holder.bind(type)

                val isSelected = selectedTypes.contains(type)
                val context = holder.itemView.context

                val bgColor = if (isSelected)
                    ContextCompat.getColor(context, R.color.primary)
                else
                    parseColor(colorBackground)

                val bgDrawable = holder.colorBackground.background.mutate()
                bgDrawable.setTint(bgColor)
                holder.colorBackground.background = bgDrawable
            }

            is AddButtonViewHolder -> {
                val bgDrawable = holder.itemView.background.mutate()
                bgDrawable.setTint(parseColor(colorBackground))
                holder.itemView.background = bgDrawable
            }
        }
    }

    override fun getItemCount(): Int = typeList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < typeList.size) TYPE else ADD_BUTTON
    }

    fun updateTypeList(newList: List<Type>) {
        typeList = newList
        selectedType = null
        notifyDataSetChanged()
    }

    fun updateTypeList(newList: List<Type>, selectedTypes: List<Type>) {
        typeList = newList
        this.selectedTypes = selectedTypes
        notifyDataSetChanged()
    }

    fun setSelectedType(type: Type?) {
        selectedType = type
        notifyDataSetChanged()
    }
}
