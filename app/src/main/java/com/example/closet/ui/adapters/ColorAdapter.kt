package com.example.closet.ui.adapters

import android.graphics.Color.parseColor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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

    private var selectedColor: ColorEntity? = null
    private var selectedColors: List<ColorEntity> = emptyList()

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorCircle: LinearLayout = itemView.findViewById(R.id.color_circle)
        private val colorText: TextView = itemView.findViewById(R.id.color_text)
        val backgroundLayout: ConstraintLayout = itemView.findViewById(R.id.color_background)

        fun bind(color: ColorEntity) {
            colorText.text = color.name

            itemView.setOnClickListener {
                selectedColor = color
                notifyDataSetChanged()
                onColorClick(color)
            }

            try {
                val parsedColor = parseColor(color.hexCode)
                colorCircle.background.setTint(parsedColor)
            } catch (e: IllegalArgumentException) {
                Log.e("ColorAdapter", "Invalid hex: ${color.hexCode}", e)
                colorCircle.background.setTint(parseColor("#808080"))
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
            TYPE_COLOR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycled_item_color, parent, false)
                ColorViewHolder(view)
            }
            TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycled_item_add, parent, false)
                AddButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ColorViewHolder -> {
                val color = colorList[position]
                holder.bind(color)

                val isSelected = selectedColors.contains(color)
                val context = holder.itemView.context

                val bgColor = if (isSelected)
                    ContextCompat.getColor(context, R.color.primary)
                else
                    parseColor(colorBackground)

                val bgDrawable = holder.backgroundLayout.background.mutate()
                bgDrawable.setTint(bgColor)
                holder.backgroundLayout.background = bgDrawable
            }

            is AddButtonViewHolder -> {
                val bgDrawable = holder.itemView.background.mutate()
                bgDrawable.setTint(parseColor(colorBackground))
                holder.itemView.background = bgDrawable
            }
        }
    }

    override fun getItemCount(): Int = colorList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < colorList.size) TYPE_COLOR else TYPE_ADD_BUTTON
    }

    fun updateColors(newColors: List<ColorEntity>) {
        this.colorList = newColors
        selectedColor = null
        notifyDataSetChanged()
    }

    fun updateColors(newColors: List<ColorEntity>, selectedColors: List<ColorEntity>) {
        this.colorList = newColors
        this.selectedColors = selectedColors
        notifyDataSetChanged()
    }

    fun setSelectedColor(color: ColorEntity?) {
        selectedColor = color
        notifyDataSetChanged()
    }
}
