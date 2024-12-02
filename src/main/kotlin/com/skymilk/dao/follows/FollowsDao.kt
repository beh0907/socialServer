package com.skymilk.dao.follows

interface FollowsDao {

    //유저 팔로우
    suspend fun followUser(follower: Long, following: Long): Boolean

    //유저 언팔로우
    suspend fun unFollowUser(follower: Long, following: Long): Boolean

    //팔로워 목록 가져오기
    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long>

    //팔로윙 목록 가져오기 (페이징)
    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long>

    //팔로윙 목록 가져오기 (전체)
    suspend fun getAllFollowing(userId: Long): List<Long>

    //팔로윙 여부 확인
    suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean

}
