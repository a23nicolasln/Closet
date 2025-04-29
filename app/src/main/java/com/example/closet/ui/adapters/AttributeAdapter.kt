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
import com.example.closet.data.model.Attribute

class AttributeAdapter(
    private var attributeList: List<Attribute>,
    private val onAttributeClick: (Attribute) -> Unit,
    private val onAddClick: () -> Unit,
    private val colorBackground: String, // Color as a string
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE = 0
    private val ADD_BUTTON = 1

    // Track the selected attribute for visual highlighting
    private var selectedAttribute: Attribute? = null
    private var selectedAttributes: List<Attribute> = emptyList()

    inner class AttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val attributeText: TextView = itemView.findViewById(R.id.attribute_text)
        val colorBackgroundLayout: ConstraintLayout = itemView.findViewById(R.id.color_background)

        fun bind(attribute: Attribute) {
            attributeText.text = attribute.name

            // Highlight if selected
            val context = itemView.context
            val primaryColor = ContextCompat.getColor(context, R.color.primary)

            // Check if this attribute is selected
            val bgColor = if (selectedAttributes.contains(attribute)) {
                primaryColor
            } else {
                parseColor(colorBackground)
            }

            // Apply background color tint
            val bgDrawable = colorBackgroundLayout.background.mutate()
            bgDrawable.setTint(bgColor)
            colorBackgroundLayout.background = bgDrawable

            itemView.setOnClickListener {
                selectedAttribute = attribute
                notifyDataSetChanged()
                onAttributeClick(attribute)
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
                AttributeViewHolder(view)
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
            is AttributeViewHolder -> {
                val attribute = attributeList[position]
                holder.bind(attribute)
            }
            is AddButtonViewHolder -> {
                val bgDrawable = holder.itemView.background.mutate()
                bgDrawable.setTint(parseColor(colorBackground))
                holder.itemView.background = bgDrawable
            }
        }
    }

    override fun getItemCount(): Int = attributeList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < attributeList.size) TYPE else ADD_BUTTON
    }

    /**
     * Updates the full attribute list.
     */
    fun updateAttributeList(newList: List<Attribute>) {
        attributeList = newList
        selectedAttribute = null
        notifyDataSetChanged()
    }

    /**
     * Optionally preselect an attribute.
     */
    fun updateAttributeList(newList: List<Attribute>, selectedAttributes: List<Attribute>) {
        attributeList = newList
        this.selectedAttributes = selectedAttributes
        notifyDataSetChanged()
    }

    /**
     * Optionally set a selected attribute.
     */
    fun setSelectedAttribute(attribute: Attribute?) {
        selectedAttribute = attribute
        notifyDataSetChanged()
    }
}
