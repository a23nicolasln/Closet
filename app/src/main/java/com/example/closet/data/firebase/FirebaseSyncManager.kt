package com.example.closet.data.firebase

import android.net.Uri
import android.util.Log
import com.example.closet.data.firebase.dto.ClothingItemDTO
import com.example.closet.data.firebase.dto.OutfitDTO
import com.example.closet.data.firebase.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

object FirebaseSyncManager {

    private val database = FirebaseDatabase.getInstance().reference

    suspend fun uploadImageToFirebaseStorage(localPath: String, storagePath: String): String {
        val uri = Uri.fromFile(File(localPath))
        val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    suspend fun prepareOutfitWithUploadedImages(originalOutfit: OutfitDTO, userId: String): OutfitDTO {
        // Upload outfit image
        val outfitImageUrl = uploadImageToFirebaseStorage(
            originalOutfit.imageUrl,
            "publishedOutfits/$userId/${UUID.randomUUID()}.jpg"
        )

        // Upload each clothing item image
        val updatedClothingItems = originalOutfit.clothingItems.map { item ->
            val uploadedUrl = uploadImageToFirebaseStorage(
                item.imgUrl,
                "publishedOutfits/$userId/clothing_items/${UUID.randomUUID()}.jpg"
            )
            item.copy(imgUrl = uploadedUrl)

        }

        return originalOutfit.copy(
            imageUrl = outfitImageUrl,
            clothingItems = updatedClothingItems
        )
    }

    suspend fun uploadOutfitToRealtimeDatabase(outfit: OutfitDTO, userId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId/publishedOutfits")
        dbRef.child(outfit.outfitId.toString()).setValue(outfit).await()
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
                    val timestamp = outfitSnapshot.child("timestamp").getValue(Long::class.java)?: 0

                    val clothingItems = mutableListOf<ClothingItemDTO>()
                    val clothingItemsSnapshot = outfitSnapshot.child("clothingItems")

                    for (itemSnapshot in clothingItemsSnapshot.children) {
                        val clothingItemId = itemSnapshot.child("clothingItemId").getValue(Long::class.java) ?: continue
                        val itemImgUrl = itemSnapshot.child("imgUrl").getValue(String::class.java) ?: ""

                        clothingItems.add(
                            ClothingItemDTO(
                                clothingItemId = clothingItemId,
                                imgUrl = itemImgUrl
                            )
                        )
                    }

                    allOutfits.add(
                        OutfitDTO(
                            outfitId = outfitId,
                            name = name,
                            imageUrl = imgUrl,
                            userId = userId,
                            clothingItems = clothingItems,
                            timestamp = timestamp
                        )
                    )
                }
            }
            callback(allOutfits.sortedByDescending { it.timestamp })
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
                val timestamp = child.child("timestamp").getValue(Long::class.java)?: 0


                val clothingItems = mutableListOf<ClothingItemDTO>()
                val clothingItemsSnapshot = child.child("clothingItems")

                for (itemSnapshot in clothingItemsSnapshot.children) {
                    val clothingItemId = itemSnapshot.child("clothingItemId").getValue(Long::class.java) ?: continue
                    val itemImgUrl = itemSnapshot.child("imgUrl").getValue(String::class.java) ?: ""

                    clothingItems.add(
                        ClothingItemDTO(
                            clothingItemId = clothingItemId,
                            imgUrl = itemImgUrl
                        )
                    )
                }

                outfitList.add(
                    OutfitDTO(
                        outfitId = outfitId,
                        name = name,
                        imageUrl = imgUrl,
                        userId = userId,
                        clothingItems = clothingItems,
                        timestamp = timestamp
                    )
                )
            }
            callback(outfitList.sortedByDescending { it.timestamp })
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching published outfits: ${exception.message}")
            callback(emptyList())
        }
    }

    fun getFollowingOutfits(userId: String, callback: (List<OutfitDTO>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/following")
        val outfitList = mutableListOf<OutfitDTO>()

        databaseRef.get().addOnSuccessListener { snapshot ->
            val followedUserIds = snapshot.children.mapNotNull { it.key }

            if (followedUserIds.isEmpty()) {
                callback(emptyList())
                return@addOnSuccessListener
            }

            var completedRequests = 0

            for (followingUserId in followedUserIds) {
                getPublishedOutfitsByUserId(followingUserId) { outfits ->
                    outfitList.addAll(outfits)
                    completedRequests++

                    if (completedRequests == followedUserIds.size) {
                        callback(outfitList.sortedByDescending { it.timestamp })
                    }
                }
            }

        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching following outfits: ${exception.message}")
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

    fun getOutfitById(outfitId: Long, userId: String, callback: (OutfitDTO?) -> Unit) {
        val outfitRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("publishedOutfits")
            .child(outfitId.toString())

        outfitRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val outfitIdValue = snapshot.child("outfitId").getValue(Long::class.java) ?: 0
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""

                    val clothingItems = mutableListOf<ClothingItemDTO>()
                    val clothingItemsSnapshot = snapshot.child("clothingItems")

                    for (itemSnapshot in clothingItemsSnapshot.children) {
                        val clothingItemId = itemSnapshot.child("clothingItemId").getValue(Long::class.java) ?: continue
                        val itemImgUrl = itemSnapshot.child("imgUrl").getValue(String::class.java) ?: ""

                        clothingItems.add(
                            ClothingItemDTO(
                                clothingItemId = clothingItemId,
                                imgUrl = itemImgUrl
                            )
                        )
                    }

                    val outfit = OutfitDTO(
                        outfitId = outfitIdValue,
                        name = name,
                        imageUrl = imageUrl,
                        userId = userId,
                        clothingItems = clothingItems
                    )
                    callback(outfit)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseSyncManager", "Error fetching outfit by ID: ${exception.message}")
                callback(null)
            }
    }

    fun followUser(currentViewingUser: String, currentUser: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$currentViewingUser/followers")
        val followerRef = FirebaseDatabase.getInstance().getReference("users/$currentUser/following")

        userRef.child(currentUser).setValue(true)
        followerRef.child(currentViewingUser).setValue(true)
    }

    fun unfollowUser(currentViewingUser: String, currentUser: String) {
        val followersRef = FirebaseDatabase.getInstance()
            .getReference("users/$currentViewingUser/followers/$currentUser")
        val followingRef = FirebaseDatabase.getInstance()
            .getReference("users/$currentUser/following/$currentViewingUser")

        followersRef.removeValue()
        followingRef.removeValue()
    }


    fun isUserFollowing(userId: String, followerId: String, callback: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId/followers")

        userRef.child(followerId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.exists())
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error checking if user is following: ${exception.message}")
            callback(false)
        }
    }
    fun observeFollowerCount(userId: String, onCountChanged: (Int) -> Unit) {
        val followersRef = FirebaseDatabase.getInstance().getReference("users/$userId/followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onCountChanged(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseSyncManager", "Error observing followers: ${error.message}")
                onCountChanged(0)
            }
        })
    }

    fun observeFollowingCount(userId: String, onCountChanged: (Int) -> Unit) {
        val followingRef = FirebaseDatabase.getInstance().getReference("users/$userId/following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onCountChanged(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseSyncManager", "Error observing following: ${error.message}")
                onCountChanged(0)
            }
        })
    }

    fun getFollowersForUser(userId: String, callback: (List<UserDTO>) -> Unit) {
        val followersRef = FirebaseDatabase.getInstance().getReference("users/$userId/followers")
        val userList = mutableListOf<UserDTO>()

        followersRef.get().addOnSuccessListener { snapshot ->
            val total = snapshot.childrenCount
            var processed = 0

            if (total == 0L) {
                callback(emptyList())
                return@addOnSuccessListener
            }

            for (followerSnapshot in snapshot.children) {
                val followerId = followerSnapshot.key ?: continue

                FirebaseDatabase.getInstance().getReference("users/$followerId").get()
                    .addOnSuccessListener { userSnapshot ->
                        val username = userSnapshot.child("username").getValue(String::class.java) ?: "Unknown"
                        val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                        val profilePictureUrl = userSnapshot.child("profilePictureUrl").getValue(String::class.java) ?: ""

                        userList.add(
                            UserDTO(
                                userId = followerId,
                                username = username,
                                email = email,
                                profilePictureUrl = profilePictureUrl
                            )
                        )
                        processed++
                        if (processed == total.toInt()) {
                            callback(userList)
                        }
                    }
                    .addOnFailureListener {
                        processed++
                        if (processed == total.toInt()) {
                            callback(userList)
                        }
                    }
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching followers: ${exception.message}")
            callback(emptyList())
        }
    }


    fun getFollowingForUser(userId: String, callback: (List<UserDTO>) -> Unit) {
        val followingRef = FirebaseDatabase.getInstance().getReference("users/$userId/following")
        val userList = mutableListOf<UserDTO>()

        followingRef.get().addOnSuccessListener { snapshot ->
            val total = snapshot.childrenCount
            var processed = 0

            if (total == 0L) {
                callback(emptyList())
                return@addOnSuccessListener
            }

            for (followingSnapshot in snapshot.children) {
                val followingId = followingSnapshot.key ?: continue

                FirebaseDatabase.getInstance().getReference("users/$followingId").get()
                    .addOnSuccessListener { userSnapshot ->
                        val username = userSnapshot.child("username").getValue(String::class.java) ?: "Unknown"
                        val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                        val profilePictureUrl = userSnapshot.child("profilePictureUrl").getValue(String::class.java) ?: ""

                        userList.add(
                            UserDTO(
                                userId = followingId,
                                username = username,
                                email = email,
                                profilePictureUrl = profilePictureUrl
                            )
                        )
                        processed++
                        if (processed == total.toInt()) {
                            callback(userList)
                        }
                    }
                    .addOnFailureListener {
                        processed++
                        if (processed == total.toInt()) {
                            callback(userList)
                        }
                    }
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching following: ${exception.message}")
            callback(emptyList())
        }
    }

    fun getUsersProfilePicture(userId: String, callback: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUrl")
        userRef.get().addOnSuccessListener { snapshot ->
            val profilePictureUrl = snapshot.getValue(String::class.java)
            callback(profilePictureUrl)
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSyncManager", "Error fetching profile picture: ${exception.message}")
            callback(null)
        }
    }

    fun likeOutfit(outfitOwnerId: String, outfitId: Long, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("FirebaseSyncManager", "User not authenticated")
            return
        }

        val likesRef = FirebaseDatabase.getInstance()
            .getReference("users/$outfitOwnerId/publishedOutfits/$outfitId/likes/$userId")

        likesRef.get().addOnSuccessListener { snapshot ->
            Log.d("FirebaseSyncManager", "Snapshot exists: ${snapshot.exists()}")
            if (snapshot.exists()) {
                // Unlike
                likesRef.removeValue().addOnSuccessListener {
                    Log.d("FirebaseSyncManager", "Successfully unliked")
                    callback(false)
                }.addOnFailureListener {
                    Log.e("FirebaseSyncManager", "Failed to unlike: ${it.message}")
                    callback(true)
                }
            } else {
                // Like
                likesRef.setValue(true).addOnSuccessListener {
                    Log.d("FirebaseSyncManager", "Successfully liked")
                    callback(true)
                }.addOnFailureListener {
                    Log.e("FirebaseSyncManager", "Failed to like: ${it.message}")
                    callback(false)
                }
            }
        }.addOnFailureListener {
            Log.e("FirebaseSyncManager", "Failed to read like state: ${it.message}")
            callback(false)
        }
    }



    fun observeLikeCount(outfitOwnerId: String, outfitId: Long, callback: (Int) -> Unit) {
        val likesRef = FirebaseDatabase.getInstance()
            .getReference("users/$outfitOwnerId/publishedOutfits/$outfitId/likes")

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseSyncManager", "Failed to observe like count: ${error.message}")
                callback(0)
            }
        })
    }

    fun isLikedOutfit(outfitOwnerId: String, outfitId: Long, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("FirebaseSyncManager", "User not authenticated")
            return
        }

        val likesRef = FirebaseDatabase.getInstance()
            .getReference("users/$outfitOwnerId/publishedOutfits/$outfitId/likes/$userId")

        likesRef.get().addOnSuccessListener { snapshot ->
            callback(snapshot.exists())
        }.addOnFailureListener {
            Log.e("FirebaseSyncManager", "Failed to check like state: ${it.message}")
            callback(false)
        }
    }

}
