package com.skymilk.repository.follows

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.user.UserDao
import com.skymilk.dao.user.UserRow
import com.skymilk.model.FollowResponse
import com.skymilk.model.FollowUserData
import com.skymilk.model.FollowsParams
import com.skymilk.model.GetFollowsResponse
import com.skymilk.util.Constants
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class FollowsRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : FollowsRepository {

    override suspend fun followUser(params: FollowsParams): Response<FollowResponse> {
        return if (followsDao.isAlreadyFollowing(params.followerId, params.followingId))
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = FollowResponse(success = false, message = "이미 팔로윙한 유저입니다.")
            )
        else {
            //팔로우 DB 갱신
            val follow = followsDao.followUser(params.followerId, params.followingId)

            if (follow) {
                //팔로우 처리 되었다면 유저 테이블 카운트 갱신
                userDao.updateFollowsCount(params.followerId, params.followingId, true)
                Response.Success(data = FollowResponse(success = true))
            } else Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = FollowResponse(success = false, message = "팔로우 처리를 실패하였습니다. 다시 시도해주세요.")
            )
        }
    }

    override suspend fun unFollowUser(params: FollowsParams): Response<FollowResponse> {
        //팔로우 DB 갱신
        val unFollow = followsDao.unFollowUser(params.followerId, params.followingId)

        return if (unFollow) {
            //팔로우 처리 되었다면 유저 테이블 카운트 갱신
            userDao.updateFollowsCount(params.followerId, params.followingId, false)

            Response.Success(data = FollowResponse(success = true))
        } else Response.Error(
            code = HttpStatusCode.InternalServerError,
            data = FollowResponse(success = false, message = "언팔로우 처리를 실패하였습니다. 다시 시도해주세요.")
        )
    }

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        //팔로워 유저 목록 아이디 가져오기
        val followersIds = followsDao.getFollowers(userId, pageNumber, pageSize)
        val followersRows = userDao.getUsers(userIds = followersIds)

        //유저 정보 -> 팔로우유저 정보 변환
        val followers = followersRows.map { followerRow ->
            //팔로윙 여부 확인
            val isFollowing = followsDao.isAlreadyFollowing(follower = userId, following = followerRow.id)
            toFollowUserData(userRow = followerRow, isFollowing = isFollowing)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = followers)
        )
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        //팔로윙 유저 목록 아이디 가져오기
        val followingIds = followsDao.getFollowing(userId, pageNumber, pageSize)
        val followingRows = userDao.getUsers(userIds = followingIds)

        //유저 정보 -> 팔로우유저 정보 변환
        val following = followingRows.map { followingRow ->
            toFollowUserData(userRow = followingRow, isFollowing = true)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = following)
        )
    }

    override suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse> {
        //팔로윙한 유저가 있는지 확인
        val hasFollowing = followsDao.getFollowing(userId = userId, pageNumber = 0, pageSize = 1).isNotEmpty()

        //이미 팔로윙한 유저가 있다면 무시
        return if (hasFollowing) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = GetFollowsResponse(success = false, message = "이미 팔로윙한 유저입니다.")
            )
        } else {
            //인기 있는 친구 추천
            val suggestedFollowingRows = userDao.getPopularUsers(limit = Constants.SUGGESTED_FOLLOWING_LIMIT)
            val suggestedFollowing = suggestedFollowingRows
                .filterNot { it.id == userId } // 나의 정보는 제외
                .map {
                    toFollowUserData(userRow = it, isFollowing = false)
                }
            return Response.Success(
                data = GetFollowsResponse(success = true, follows = suggestedFollowing)
            )
        }
    }

    private fun toFollowUserData(userRow: UserRow, isFollowing: Boolean): FollowUserData {
        return FollowUserData(
            id = userRow.id,
            name = userRow.name,
            bio = userRow.bio,
            imageUrl = userRow.imageUrl,
            isFollowing = isFollowing
        )
    }
}
