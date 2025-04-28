package com.example.closet.ui.adapters

import android.graphics.Color.parseColor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.closet.R
import com.example.closet.data.model.Color as ColorEntity

class ColorAdapter(
    private var colorList: List<ColorEntity>,
    private val onColorClick: (ColorEntity) -> Unit,
    private val onAddClick: () -> Unit,
    private val colorBackground: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_COLOR = 0
    private val TYPE_ADD_BUTTON = 1

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorCircle: LinearLayout = itemView.findViewById(R.id.color_circle)
        val colorText: TextView = itemView.findViewById(R.id.color_text)
        val colorBackground: ConstraintLayout = itemView.findViewById(R.id.color_background)

        init {
            itemView.setOnClickListener {
                val colorItem = colorList[adapterPosition]
                onColorClick(colorItem) // Pass the color item to the click listener
            }
        }
    }

    inner class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<View>(R.id.add_button).setOnClickListener {
                onAddClick() // Call the add button action
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COLOR -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycled_item_color, parent, false)
                ColorViewHolder(view)
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
            is ColorViewHolder -> {
                val colorItem = colorList[position]
                holder.colorText.text = colorItem.name

                val bgDrawable = holder.colorBackground.background.mutate()
                bgDrawable.setTint(parseColor(colorBackground))
                holder.colorBackground.background = bgDrawable

                try {
                    val color = parseColor(colorItem.hexCode)
                    holder.colorCircle.background.setTint(color)
                } catch (e: IllegalArgumentException) {
                    Log.e("ColorAdapter", "Invalid hex: ${colorItem.hexCode}", e)
                    holder.colorCircle.background.setTint(parseColor("#808080")) // Fallback gray
                }
            }
            is AddButtonViewHolder -> {
                val bgDrawable = holder.itemView.background.mutate()
                bgDrawable.setTint(parseColor(colorBackground))
                holder.itemView.background = bgDrawable
            }
        }
    }

    override fun getItemCount(): Int = colorList.size + 1 // +1 for the add button

    override fun getItemViewType(position: Int): Int {
        return if (position < colorList.size) {
            TYPE_COLOR
        } else {
            TYPE_ADD_BUTTON
        }
    }

    fun updateColors(newColors: List<ColorEntity>) {
        this.colorList = newColors
        notifyDataSetChanged()
    }

}
