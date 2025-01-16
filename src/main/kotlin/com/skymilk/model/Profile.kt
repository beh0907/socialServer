package com.skymilk.model

import kotlinx.serialization.Serializable


@Serializable
data class UpdateUserParams(
    val userId: Long,
    val name: String,
    val bio: String,
    val fileName: String? = null,
)

@Serializable
data class Profile(
    val userId: Long,
    val name: String,
    val bio: String,
    val fileName: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val isOwnProfile: Boolean = false,
)

@Serializable
data class ProfileResponse(
    val success: Boolean,
    val profile: Profile? = null,
    val message: String? = null,
)
