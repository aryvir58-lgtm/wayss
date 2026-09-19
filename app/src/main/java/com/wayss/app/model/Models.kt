package com.wayss.app.model

enum class UserRole { USER, ADMIN }

data class WayssUser(
    val uid: String = "",
    val username: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String = "",
    val role: UserRole = UserRole.USER,
    val verified: Boolean = false,
    val creatorBadge: Boolean = false,
    val privateAccount: Boolean = false
)

data class Post(
    val id: String = "",
    val ownerUid: String = "",
    val mediaUrl: String = "",
    val caption: String = "",
    val hashtags: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val likeCount: Long = 0,
    val commentCount: Long = 0
)

data class VerificationRequest(
    val id: String = "",
    val uid: String = "",
    val username: String = "",
    val displayName: String = "",
    val category: String = "",
    val reason: String = "",
    val status: String = "PENDING",
    val createdAt: Long = 0L
)
