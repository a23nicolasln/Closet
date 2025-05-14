package com.example.closet.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.dto.UserDTO

class UserSearchAdapter(
    private var userList: List<UserDTO>,
    private val onUserClick: (UserDTO) -> Unit
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePicture: ImageView = view.findViewById(R.id.profile_picture)
        val usernameTextView: TextView = view.findViewById(R.id.username_text)

        fun bind(user: UserDTO) {
            usernameTextView.text = user.username

            Glide.with(profilePicture.context)
                .load(user.profilePictureUrl)
                .placeholder(R.drawable.icon_account)
                .error(R.drawable.icon_account)
                .centerCrop()
                .into(profilePicture)

            itemView.setOnClickListener {
                onUserClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycled_item_profile_serch, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateUsers(newList: List<UserDTO>) {
        userList = newList
        notifyDataSetChanged()
    }
}
