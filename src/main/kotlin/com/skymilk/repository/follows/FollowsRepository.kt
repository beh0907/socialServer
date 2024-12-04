package com.skymilk.repository.follows

import com.skymilk.model.FollowsParams
import com.skymilk.model.FollowResponse
import com.skymilk.model.GetFollowsResponse
import com.skymilk.util.Response

interface FollowsRepository {
    //팔로우 설정
    suspend fun followUser(params: FollowsParams): Response<FollowResponse>

    //언팔로우 설정
    suspend fun unFollowUser(params: FollowsParams): Response<FollowResponse>

    //팔로워 목록 가져오기
    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    //팔로윙 목록 가져오기
    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    //추천 목록 제공 여
    suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse>
}
