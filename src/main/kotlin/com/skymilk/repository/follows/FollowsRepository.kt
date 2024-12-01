package com.skymilk.repository.follows

import com.skymilk.model.FollowsParams
import com.skymilk.model.FollowsResponse
import com.skymilk.util.Response

interface FollowsRepository {
    suspend fun followUser(params: FollowsParams): Response<FollowsResponse>
    suspend fun unFollowUser(params: FollowsParams): Response<FollowsResponse>
}
