package com.skymilk.repository.postComments

import com.skymilk.model.AddCommentParams
import com.skymilk.model.CommentResponse
import com.skymilk.model.CommentsResponse
import com.skymilk.model.RemoveCommentParams
import com.skymilk.util.Response

interface PostCommentsRepository {

    //댓글 작성
    suspend fun addComment(params: AddCommentParams): Response<CommentResponse>

    //댓글 삭제
    suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse>

    //댓글 목록 가져오기
    suspend fun getComments(postId: Long, pageNumber: Int, pageSize: Int): Response<CommentsResponse>
}
