package com.skymilk.repository.postLikes

import com.skymilk.model.LikeParams
import com.skymilk.model.LikeResponse
import com.skymilk.util.Response

interface PostLikesRepository {

    //좋아요
    suspend fun addLike(params:LikeParams): Response<LikeResponse>

    //좋아요 제거
    suspend fun removeLike(params:LikeParams): Response<LikeResponse>
}
