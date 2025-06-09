package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.dto.OutfitDTO
import com.google.firebase.database.FirebaseDatabase

class OutfitWithProfilePictureAdapter(
    private var dataSet: List<OutfitDTO>,
    private val onItemClick: (OutfitDTO) -> Unit,
    private val showProfilePicture: Boolean
) : RecyclerView.Adapter<OutfitWithProfilePictureAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val outfitImageView: ImageView = view.findViewById(R.id.item_image)
        val profilePictureView: ImageView = view.findViewById(R.id.profile_picture)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycled_item_with_profile_picture, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val outfit = dataSet[position]
        val context = viewHolder.outfitImageView.context

        // Load outfit image
        if (outfit.imageUrl.isEmpty()) {
            viewHolder.outfitImageView.setImageResource(R.drawable.placeholder_image)
        } else {
            Glide.with(context)
                .load(outfit.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(viewHolder.outfitImageView)
        }

        // Conditionally show profile picture
        if (showProfilePicture) {
            viewHolder.profilePictureView.visibility = View.VISIBLE
            val userId = outfit.userId
            if (userId.isNotEmpty()) {
                val userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("profilePictureUrl")

                userRef.get().addOnSuccessListener { snapshot ->
                    val profileUrl = snapshot.getValue(String::class.java)
                    if (!profileUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(profileUrl)
                            .placeholder(R.drawable.icon_account)
                            .error(R.drawable.icon_account)
                            .into(viewHolder.profilePictureView)
                    } else {
                        viewHolder.profilePictureView.setImageResource(R.drawable.icon_account)
                    }
                }.addOnFailureListener {
                    viewHolder.profilePictureView.setImageResource(R.drawable.icon_account)
                }
            } else {
                viewHolder.profilePictureView.setImageResource(R.drawable.icon_account)
            }
        } else {
            viewHolder.profilePictureView.visibility = View.GONE
        }

        viewHolder.outfitImageView.setOnClickListener {
            onItemClick(outfit)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateItems(newItems: List<OutfitDTO>) {
        dataSet = newItems
        notifyDataSetChanged()
    }
}

