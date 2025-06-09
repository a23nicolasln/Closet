package com.example.closet.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.firebase.FirebaseSyncManager
import com.example.closet.data.firebase.FirebaseSyncManager.getUsersProfilePicture
import com.example.closet.data.firebase.FirebaseSyncManager.isLikedOutfit
import com.example.closet.ui.adapters.ClothingItemDTOAdapterSmall
import com.example.closet.ui.adapters.CommentAdapter
import java.net.URLEncoder
import kotlin.properties.Delegates

class OutfitViewSocialFragment : Fragment() {

    private var outfitId by Delegates.notNull<Long>()
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            outfitId = it.getLong("outfitId", 0L)
            userId = it.getString("userId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_outfit_view_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val outfit = null

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val recyclerClothingItems = view.findViewById<RecyclerView>(R.id.recycler_view_clothing_items)
        recyclerClothingItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val clothingItemAdapter = ClothingItemDTOAdapterSmall(
            dataSet = emptyList(),
            onItemClick = { clothingItem ->
                val context = requireContext()
                val url = clothingItem.link

                if (url.isNullOrBlank()) {
                    Toast.makeText(context, "User didn't add link", Toast.LENGTH_SHORT).show()
                    return@ClothingItemDTOAdapterSmall
                }

                val formattedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                    url
                } else {
                    "https://$url"
                }

                val chromeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                    setPackage("com.android.chrome")
                }

                try {
                    context.startActivity(chromeIntent)
                } catch (e: ActivityNotFoundException) {
                    // Fallback if Chrome is not installed
                    val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
                    context.startActivity(fallbackIntent)
                }
            }


        )
        recyclerClothingItems.adapter = clothingItemAdapter

        val outfitImage = view.findViewById<ImageView>(R.id.imageViewOutfit)
        //val outfitName = view.findViewById<TextView>(R.id.outfitName)

        FirebaseSyncManager.getOutfitById(outfitId, userId) { outfit ->
            if (outfit != null) {
                Glide.with(requireContext())
                    .load(outfit.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(outfitImage)

                clothingItemAdapter.updateItems(outfit.clothingItems)
            } else {
                clothingItemAdapter.updateItems(emptyList())
            }

        }


        // Retrive social iteraction buttons
        val likeButton = view.findViewById<ImageView>(R.id.like_button)
        val commentButton = view.findViewById<ImageView>(R.id.comment_button)
        val likecount = view.findViewById<TextView>(R.id.like_count)
        val accountButton = view.findViewById<ImageView>(R.id.account_button)
        val itemsButton = view.findViewById<ImageView>(R.id.items_button)



        FirebaseSyncManager.observeLikeCount(userId, outfitId) { likeCount ->
            likecount.text = likeCount.toString()
        }

        getUsersProfilePicture(userId) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_account)
                    .into(accountButton)
            }
        }

        isLikedOutfit(userId, outfitId) { isLiked ->
            if (isLiked) {
                likeButton.setImageResource(R.drawable.icon_liked)
                likeButton.setPadding(0, 10, 0, 10)
            } else {
                likeButton.setImageResource(R.drawable.icon_like)
            }
        }

        likeButton.setOnClickListener {
            Log.d("OutfitViewSocialFragment", "Like button clicked")
            FirebaseSyncManager.likeOutfit(userId,outfitId) { isLiked ->
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.icon_liked)
                    likeButton.setPadding(0, 10, 0, 10)
                } else {
                    likeButton.setImageResource(R.drawable.icon_like)
                }
            }
        }

        val commentInputLayout = view.findViewById<LinearLayout>(R.id.comment_input_layout)

        commentButton.setOnClickListener {
            commentInputLayout.visibility = View.VISIBLE
            recyclerClothingItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerClothingItems.setPadding(0, 0, 0, 200)
            recyclerClothingItems.adapter = CommentAdapter(
                comments = emptyList(),
                onProfileClick = { comment ->
                    val action = OutfitViewSocialFragmentDirections.actionOutfitViewSocialFragmentToUserProfileFragment(comment.userId)
                    findNavController().navigate(action)
                }
            )
            FirebaseSyncManager.getCommentsForOutfit(
                outfitOwnerId = userId,
                outfitId = outfitId
            ) { comments ->
                (recyclerClothingItems.adapter as CommentAdapter).updateItems(comments.sortedByDescending { it.timestamp })
            }
        }

        itemsButton.setOnClickListener {
            commentInputLayout.visibility = View.GONE
            recyclerClothingItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerClothingItems.adapter = clothingItemAdapter
            FirebaseSyncManager.getOutfitById(outfitId, userId) { outfit ->
                if (outfit != null) {
                    clothingItemAdapter.updateItems(outfit.clothingItems)
                } else {
                    clothingItemAdapter.updateItems(emptyList())
                }
            }
        }

        accountButton.setOnClickListener {
            val action = outfitId.let {
                OutfitViewSocialFragmentDirections.actionOutfitViewSocialFragmentToUserProfileFragment(
                    userId
                )
            }
            findNavController().navigate(action)
        }

        val send = view.findViewById<ImageView>(R.id.send_comment_button)
        val commentInput = view.findViewById<EditText>(R.id.comment_edit_text)

        send.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                FirebaseSyncManager.uploadComment(
                    outfitOwnerId = userId,
                    OutfitID = outfitId,
                    comment = commentText,
                    callback = { success ->
                        if (success) {
                            commentInput.text.clear()
                            commentInput.clearFocus()
                            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(commentInput.windowToken, 0)

                            // Refresh comments
                            FirebaseSyncManager.getCommentsForOutfit(
                                outfitOwnerId = userId,
                                outfitId = outfitId
                            ) { comments ->
                                (recyclerClothingItems.adapter as CommentAdapter).updateItems(comments)
                            }
                        } else {
                            Log.e("OutfitViewSocialFragment", "Failed to upload comment")
                        }
                    }
                )
            }
        }
    }
}
