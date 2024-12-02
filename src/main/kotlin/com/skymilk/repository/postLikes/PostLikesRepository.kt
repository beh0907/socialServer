package com.skymilk.repository.postLikes

interface PostLikesRepository {

    //좋아요
    suspend fun addLike(userId: Long, postId: Long): Boolean

    //좋아요 제거
    suspend fun removeLike(userId: Long, postId: Long): Boolean
}
