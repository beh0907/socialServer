package com.skymilk.repository.postComments

import com.skymilk.dao.post.PostDao
import com.skymilk.dao.postComments.PostCommentRow
import com.skymilk.dao.postComments.PostCommentsDao
import com.skymilk.model.AddCommentParams
import com.skymilk.model.CommentResponse
import com.skymilk.model.CommentsResponse
import com.skymilk.model.PostComment
import com.skymilk.model.RemoveCommentParams
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class PostCommentsRepositoryImpl(
    private val postDao: PostDao,
    private val postCommentsDao: PostCommentsDao,
) : PostCommentsRepository {


    override suspend fun addComment(params: AddCommentParams): Response<CommentResponse> {
        val postCommentRow = postCommentsDao.addComment(
            postId = params.postId,
            userId = params.userId,
            content = params.content
        )

        return if (postCommentRow == null) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = CommentResponse(success = false, message = "댓글 추가가 실패하였습니다.")
            )
        } else {
            //댓글이 추가되었다면 댓글 수를 갱신한다
            postDao.updateCommentsCount(postId = params.postId)

            Response.Success(
                data = CommentResponse(success = true, comment = toPostComment(postCommentRow))
            )
        }
    }

    override suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse> {
        val commentRow = postCommentsDao.findComment(commentId = params.commentId, postId = params.postId)

        return if (commentRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = CommentResponse(success = false, message = "삭제할 댓글을 찾지 못하였습니다.")
            )
        } else {
            val postOwnerId = postDao.getPost(postId = params.postId)!!.userId

            //삭제 권한이 없다면
            if (params.userId != commentRow.userId && params.userId != postOwnerId) {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = CommentResponse(
                        success = false,
                        message = "타인이 작성한 댓글을 삭제할 수 없습니다."
                    )
                )
            } else {
                val commentWasRemoved = postCommentsDao.removeComment(commentId = params.commentId, postId = params.postId)

                if (commentWasRemoved) {
                    //삭제 후 댓글 수 갱신
                    postDao.updateCommentsCount(postId = params.postId, decrement = true)

                    Response.Success(data = CommentResponse(success = true))
                } else {
                    Response.Error(
                        code = HttpStatusCode.Conflict,
                        data = CommentResponse(
                            success = false,
                            message = "댓글 ${params.commentId}를 삭제할 수 없습니다."
                        )
                    )
                }
            }
        }
    }

    override suspend fun getComments(
        postId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<CommentsResponse> {
        val commentRows = postCommentsDao.getComments(postId = postId, pageNumber = pageNumber, pageSize = pageSize)
        val comments = commentRows.map {
            toPostComment(it)
        }

        return Response.Success(
            data = CommentsResponse(success = true, comments = comments)
        )
    }

    private fun toPostComment(commentRow: PostCommentRow): PostComment {
        return PostComment(
            commentId = commentRow.commentId,
            content = commentRow.content,
            postId = commentRow.postId,
            userId = commentRow.userId,
            userName = commentRow.userName,
            userImageFileName = commentRow.userImageFileName,
            createdAt = commentRow.createdAt,
        )
    }
}
