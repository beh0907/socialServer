package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowResponse(
    val success: Boolean,
    val message: String? = null,
)

@Serializable
data class GetFollowsResponse(
    val success: Boolean,
    val follows: List<FollowUserData> = emptyList(),
    val message: String? = null,
)

@Serializable
data class FollowsParams(
    val followerId: Long,
    val followingId: Long,
)

@Serializable
data class FollowUserData(
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null,
    val isFollowing: Boolean = false,
)
