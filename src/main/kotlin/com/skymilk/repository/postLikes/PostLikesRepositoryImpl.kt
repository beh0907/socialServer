package com.skymilk.repository.postLikes

import com.skymilk.dao.post.PostDao
import com.skymilk.dao.postLikes.PostLikesDao
import com.skymilk.model.LikeParams
import com.skymilk.model.LikeResponse
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class PostLikesRepositoryImpl(
    private val postLikesDao: PostLikesDao,
    private val postDao: PostDao,
) : PostLikesRepository {
    override suspend fun addLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = postLikesDao.isPostLikeByUser(params.postId, params.userId)

        return if (likeExists) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = LikeResponse(success = false, message = "이미 좋아요를 설정하셨습니다.")
            )
        } else {
            //좋아요 설정
            val postLiked = postLikesDao.addLike(params.userId, params.postId)
            if (postLiked) {
                //게시글 좋아요 필드 값 증가
                postDao.updateLikesCount(params.postId)
                Response.Success(data = LikeResponse(success = true))
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "좋아요 설정을 하지 못하였습니다. 다시 시도해주세요.")
                )
            }
        }
    }

    override suspend fun removeLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = postLikesDao.isPostLikeByUser(params.postId, params.userId)

        return if (likeExists) {
            //좋아요 제거
            val likeRemoved = postLikesDao.removeLike(params.userId, params.postId)
            if (likeRemoved) {
                //게시글 좋아요 필드 값 감소
                postDao.updateLikesCount(params.postId, decrement = true)

                Response.Success(data = LikeResponse(success = true))
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "좋아요 설정을 하지 못하였습니다. 다시 시도해주세요.")
                )
            }
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = LikeResponse(success = false, message = "이미 좋아요를 설정하셨습니다.")
            )
        }
    }
}
