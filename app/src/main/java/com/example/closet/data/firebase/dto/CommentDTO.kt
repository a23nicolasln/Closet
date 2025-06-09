package com.example.closet.data.firebase.dto

data class CommentDTO(
    val userId: String = "",
    val outfitId: Long = 0L,
    val commentText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
