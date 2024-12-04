package com.skymilk.dao.user

interface UserDao {
    //회원가입
    suspend fun insert(name: String, email: String, password: String): UserRow?

    //이메일로 유저 정보 찾기
    suspend fun findByEmail(email: String): UserRow?

    //ID로 유저 정보 찾기
    suspend fun findById(userId: Long): UserRow?

    //유저 정보 갱신
    suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String?): Boolean

    //팔로우 수 갱신
    suspend fun updateFollowsCount(follower: Long, followed: Long, isFollower: Boolean): Boolean

    //조건 유저 목록 가져오기
    suspend fun getUsers(userIds: List<Long>): List<UserRow>

    //인기 있는 유저 목록 가져오기
    suspend fun getPopularUsers(limit: Int): List<UserRow>
}
