package com.skymilk.dao.user

import com.skymilk.model.SignUpParams

interface UserDao {
    //회원가입
    suspend fun insert(params: SignUpParams): UserRow?

    //이메일로 정보 여부 찾기
    suspend fun findByEmail(email: String): UserRow?

    //팔로우 수 갱신
    suspend fun updateFollowsCount(follower: Long, followed: Long, isFollower: Boolean): Boolean
}
