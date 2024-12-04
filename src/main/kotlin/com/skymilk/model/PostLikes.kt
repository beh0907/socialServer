package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse(
    val success: Boolean,
    val message: String? = null,
)

@Serializable
data class LikeParams(
    val userId: Long,
    val postId: Long,
)
