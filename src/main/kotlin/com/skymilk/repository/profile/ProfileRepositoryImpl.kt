package com.skymilk.repository.profile

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.user.UserDao
import com.skymilk.dao.user.UserRow
import com.skymilk.model.Profile
import com.skymilk.model.ProfileResponse
import com.skymilk.model.UpdateUserParams
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class ProfileRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : ProfileRepository {
    override suspend fun getUserById(
        userId: Long,
        currentUserId: Long,
    ): Response<ProfileResponse> {
        val userRow = userDao.findById(userId)

        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "선택한 아이디를 가진 유저를 찾지 못하였습니다."
                )
            )
        } else {
            val isFollowing = followsDao.isAlreadyFollowing(follower = currentUserId, following = userId)
            val isOwnProfile = userId == currentUserId

            //유저 정보 리턴
            Response.Success(
                data = ProfileResponse(
                    success = true,
                    profile = toProfile(userRow, isFollowing, isOwnProfile)
                )
            )
        }
    }

    override suspend fun updateUser(params: UpdateUserParams): Response<ProfileResponse> {
        val userExists = userDao.findById(params.userId) != null

        if (userExists) {
            val userUpdate = userDao.updateUser(
                userId = params.userId,
                name = params.name,
                bio = params.bio,
                imageUrl = params.imageUrl,
            )

            return if (userUpdate) Response.Success(data = ProfileResponse(success = true))
            else Response.Error(
                code = HttpStatusCode.Conflict,
                data = ProfileResponse(success = false, message = "유저 정보를 갱신하지 못했습니다.")
            )
        } else {
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(success = false, message = "선택한 아이디를 가진 유저를 찾지 못하였습니다.")
            )
        }
    }
}

private fun toProfile(userRow: UserRow, isFollowing: Boolean, isOwnProfile: Boolean): Profile {
    return Profile(
        userId = userRow.id,
        name = userRow.name,
        bio = userRow.bio,
        imageUrl = userRow.imageUrl,
        followersCount = userRow.followersCount,
        followingCount = userRow.followingCount,
        isFollowing = isFollowing,
        isOwnProfile = isOwnProfile
    )
}
