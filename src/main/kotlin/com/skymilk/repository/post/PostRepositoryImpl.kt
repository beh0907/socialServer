package com.skymilk.repository.post

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.post.PostDao
import com.skymilk.dao.post.PostRow
import com.skymilk.dao.postLikes.PostLikesDao
import com.skymilk.model.Post
import com.skymilk.model.PostParam
import com.skymilk.model.PostResponse
import com.skymilk.model.PostUpdateParam
import com.skymilk.model.PostsResponse
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val postLikesDao: PostLikesDao,
    private val followsDao: FollowsDao,
) : PostRepository {

    override suspend fun createPost(
        fileNames: List<String>,
        params: PostParam,
    ): Response<PostResponse> {
        val createdPost = postDao.createPost(caption = params.caption, fileNames = fileNames, userId = params.userId)

        return if (createdPost != null) {
            Response.Success(
                data = PostResponse(
                    success = true,
                    post = toPost(
                        postRow = createdPost,
                        isPostLiked = false,
                        isOwnPost = true
                    )
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "게시글을 추가하지 못하였습니다. 다시 시도해주세요."
                )
            )
        }
    }

    override suspend fun updatePost(
        addFileNames: List<String>,
        params: PostUpdateParam,
    ): Response<PostResponse> {
        val updatedPost = postDao.updatePost(
            caption = params.caption,
            fileNames = params.fileNames + addFileNames, // 기존 이미지와 새로 저장된 이미지 통합
            userId = params.userId,
            postId = params.postId
        )

        return if (updatedPost) {
            Response.Success(data = PostResponse(success = true))
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "게시글을 수정하지 못하였습니다. 다시 시도해주세요."
                )
            )
        }
    }

    override suspend fun getFeedsPost(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<PostsResponse> {
        val followUsers = followsDao.getAllFollowing(userId) + userId // 나의 게시물도 표시하기 위해 추가
        val postsRows = postDao.getFeedsPost(userId, followUsers, pageNumber, pageSize)

        //게시물 매핑 및 추가 정보 적용
        val posts = postsRows.map { postRow ->
            toPost(
                postRow = postRow,
                isPostLiked = postLikesDao.isPostLikeByUser(userId, postRow.postId),
                isOwnPost = postRow.userId == userId
            )
        }

        return Response.Success(data = PostsResponse(success = true, posts = posts))
    }

    override suspend fun getPostsByUser(
        postOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<PostsResponse> {
        val postsRows = postDao.getPostsByUser(postOwnerId, pageNumber, pageSize)

        //게시물 매핑 및 추가 정보 적용
        val posts = postsRows.map { postRow ->
            toPost(
                postRow = postRow,
                isPostLiked = postLikesDao.isPostLikeByUser(currentUserId, postRow.postId),
                isOwnPost = postRow.userId == currentUserId
            )
        }

        return Response.Success(data = PostsResponse(success = true, posts = posts))
    }

    override suspend fun getPost(
        postId: Long,
        currentUserId: Long,
    ): Response<PostResponse> {
        val post = postDao.getPost(postId)

        return if (post == null) {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "게시글을 찾을 수 없습니다."
                )
            )
        } else {
            val isPostLiked = postLikesDao.isPostLikeByUser(currentUserId, postId)
            val isOwnPost = post.userId == currentUserId

            Response.Success(
                data = PostResponse(
                    success = true,
                    post = toPost(post, isPostLiked, isOwnPost)
                )
            )
        }
    }

    override suspend fun deletePost(postId: Long): Response<PostResponse> {
        val postIsDeleted = postDao.deletePost(postId)

        return if (postIsDeleted) {
            Response.Success(data = PostResponse(success = true))
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "게시글을 삭제하지 못하였습니다. 다시 시도해주세요."
                )
            )
        }
    }


    private fun toPost(postRow: PostRow, isPostLiked: Boolean, isOwnPost: Boolean): Post {
        return Post(
            postId = postRow.postId,
            caption = postRow.caption,
            fileNames = postRow.fileNames,
            createdAt = postRow.createdAt,
            likesCount = postRow.likesCount,
            commentsCount = postRow.commentsCount,
            userId = postRow.userId,
            userImageFileName = postRow.userImageFileName,
            userName = postRow.userName,
            isLiked = isPostLiked,
            isOwnPost = isOwnPost
        )
    }
}
