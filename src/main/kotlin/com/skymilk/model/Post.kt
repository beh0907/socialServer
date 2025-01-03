package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val success: Boolean,
    val post: Post? = null,
    val message: String? = null,
)

@Serializable
data class PostUpdateResponse(
    val success: Boolean,
    val imageUrl: String? = null,
    val message: String? = null,
)

@Serializable
data class PostsResponse(
    val success: Boolean,
    val posts: List<Post> = emptyList(),
    val message: String? = null,
)

@Serializable
data class PostParam(
    val caption: String,
    val userId: Long,
)

@Serializable
data class PostUpdateParam(
    val caption: String,
    val imageUrl: String,
    val userId: Long,
    val postId: Long,
)


@Serializable
data class Post(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
    val isLiked: Boolean,
    val isOwnPost: Boolean,
)
