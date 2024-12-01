package com.skymilk.repository.follows

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.user.UserDao
import com.skymilk.model.FollowsParams
import com.skymilk.model.FollowsResponse
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class FollowsRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : FollowsRepository {

    override suspend fun followUser(params: FollowsParams): Response<FollowsResponse> {
        return if (followsDao.isAlreadyFollowing(params.followerId, params.followingId))
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = FollowsResponse(success = false, message = "이미 팔로윙한 유저입니다.")
            )
        else {
            //팔로우 DB 갱신
            val follow = followsDao.followUser(params.followerId, params.followingId)

            if (follow) {
                //팔로우 처리 되었다면 유저 테이블 카운트 갱신
                userDao.updateFollowsCount(params.followerId, params.followingId, true)
                Response.Success(data = FollowsResponse(success = true))
            } else Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = FollowsResponse(success = false, message = "팔로우 처리를 실패하였습니다. 다시 시도해주세요.")
            )
        }
    }

    override suspend fun unFollowUser(params: FollowsParams): Response<FollowsResponse> {
        //팔로우 DB 갱신
        val unFollow = followsDao.unFollowUser(params.followerId, params.followingId)

        return if (unFollow) {
            //팔로우 처리 되었다면 유저 테이블 카운트 갱신
            userDao.updateFollowsCount(params.followerId, params.followingId, false)

            Response.Success(data = FollowsResponse(success = true))
        }
        else Response.Error(
            code = HttpStatusCode.InternalServerError,
            data = FollowsResponse(success = false, message = "언팔로우 처리를 실패하였습니다. 다시 시도해주세요.")
        )
    }
}
