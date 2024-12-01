package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowsResponse(
    val success: Boolean,
    val message: String? = null,
)

@Serializable
data class FollowsParams(
    val followerId: Long,
    val followingId: Long,
    val isFollowing: Boolean,
)
