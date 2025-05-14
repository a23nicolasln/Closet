package com.example.closet.data.firebase

import android.util.Log
import com.example.closet.data.firebase.dto.ClothingItemDTO
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.data.firebase.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseSyncManager {

    private val database = FirebaseDatabase.getInstance().reference


    fun publishOutfit(outfit: OutfitDTO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database
                .child("users")
                .child(userId)
                .child("publishedOutfits")
                .child(outfit.outfitId.toString())
                .setValue(outfit)
        } else {
            Log.e("Firebase", "User not authenticated")
        }
    }

    fun getAllPublishedOutfits(callback: (List<OutfitDTO>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        val allOutfits = mutableListOf<OutfitDTO>()

        databaseRef.get().addOnSuccessListener { snapshot ->
            for (userSnapshot in snapshot.children) {
                val userId = userSnapshot.key ?: continue
                val publishedOutfits = userSnapshot.child("publishedOutfits")

                for (outfitSnapshot in publishedOutfits.children) {
                    val outfitId = outfitSnapshot.child("outfitId").getValue(Long::class.java) ?: continue
                    val name = outfitSnapshot.child("name").getValue(String::class.java) ?: "Unnamed Outfit"
                    val imgUrl = outfitSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                    val clothingItemsSnapshot = outfitSnapshot.child("clothingItems")
                    val clothingItems = mutableListOf<ClothingItemDTO>()

                    val outfit = OutfitDTO(
                        outfitId = outfitId,
                        name = name,
                        imageUrl = imgUrl,
                        userId = userId,
                        clothingItems = emptyList()
                    )
                    allOutfits.add(outfit)
                }
            }
            callback(allOutfits)
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching published outfits: ${exception.message}")
            callback(emptyList())
        }
    }

    fun getPublishedOutfitsByUserId(userId: String, callback: (List<OutfitDTO>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/publishedOutfits")
        val outfitList = mutableListOf<OutfitDTO>()

        databaseRef.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val outfitId = child.child("outfitId").getValue(Long::class.java) ?: continue
                val name = child.child("name").getValue(String::class.java) ?: "Unnamed Outfit"
                val imgUrl = child.child("imageUrl").getValue(String::class.java) ?: ""
                val clothingItemsSnapshot = child.child("clothingItems")
                val clothingItems = mutableListOf<ClothingItemDTO>()

                val outfit = OutfitDTO(
                    outfitId = outfitId,
                    name = name,
                    imageUrl = imgUrl,
                    userId = userId,
                    clothingItems = emptyList()
                )
                outfitList.add(outfit)
            }
            callback(outfitList)
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching published outfits: ${exception.message}")
            callback(emptyList())
        }
    }

    fun getUsersByUsernamePrefix(prefix: String, callback: (List<UserDTO>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")

        val query = databaseRef
            .orderByChild("username")
            .startAt(prefix)
            .endAt(prefix + "\uf8ff")  // Unicode trick to include all results starting with `prefix`

        query.get()
            .addOnSuccessListener { snapshot ->
                val userList = mutableListOf<UserDTO>()

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val username = userSnapshot.child("username").getValue(String::class.java) ?: continue
                    val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                    val profilePictureUrl = userSnapshot.child("profilePictureUrl").getValue(String::class.java) ?: ""

                    userList.add(
                        UserDTO(
                            userId = userId,
                            username = username,
                            email = email,
                            profilePictureUrl = profilePictureUrl
                        )
                    )
                }

                callback(userList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseSyncManager", "Error fetching users by username prefix: ${exception.message}")
                callback(emptyList())
            }
    }


}
