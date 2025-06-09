package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.dto.CommentDTO
import com.example.closet.data.firebase.dto.UserDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(
    private var comments: List<CommentDTO>,
    private val onProfileClick: (CommentDTO) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: ImageView = itemView.findViewById(R.id.profile_picture)
        val usernameText: TextView = itemView.findViewById(R.id.username_text)
        val commentDate: TextView = itemView.findViewById(R.id.comment_date)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val topRightConstraint: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycled_item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        // Get user profile picture
        FirebaseSyncManager.getUserById(comment.userId) { user: UserDTO? ->
            if (user != null) {
                Glide.with(holder.itemView.context)
                    .load(user.profilePictureUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.profilePicture)
                holder.usernameText.text = user.username.take(10)

            } else {
                holder.profilePicture.setImageResource(R.drawable.placeholder_image)
            }
        }

        holder.usernameText.text = "${comment.userId.take(10)}"
        holder.commentText.text = comment.commentText
        holder.commentDate.text = formatDate(comment.timestamp)

        holder.topRightConstraint.setOnClickListener {
            onProfileClick(comment)
        }
    }

    override fun getItemCount(): Int = comments.size

    fun updateItems(newComments: List<CommentDTO>) {
        comments = newComments
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return format.format(date)
    }
}

