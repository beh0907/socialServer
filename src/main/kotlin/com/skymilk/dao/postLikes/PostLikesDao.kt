package com.skymilk.dao.postLikes

interface PostLikesDao {

    //좋아요
    suspend fun addLike(userId: Long, postId: Long):Boolean

    //좋아요 제거
    suspend fun removeLike(userId: Long, postId: Long):Boolean

    //유저별 좋아요 여부 체크
    suspend fun isPostLikeByUser(userId: Long, postId: Long):Boolean
}
