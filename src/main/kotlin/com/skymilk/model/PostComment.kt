package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val success: Boolean,
    val comment: PostComment? = null,
    val message: String? = null,
)

@Serializable
data class CommentsResponse(
    val success: Boolean,
    val comments: List<PostComment> = emptyList(),
    val message: String? = null,
)

@Serializable
data class AddCommentParams(
    val content: String,
    val userId: Long,
    val postId: Long,
)

@Serializable
data class RemoveCommentParams(
    val commentId: Long,
    val userId: Long,
    val postId: Long,
)


@Serializable
data class PostComment(
    val commentId: Long,
    val content: String,
    val postId: Long,
    val userId: Long,
    val userName: String,
    val userImageFileName: String?,
    val createdAt: String,
)
