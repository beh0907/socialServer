package com.skymilk.dao.postComments

interface PostCommentsDao {

    //댓글 작성
    suspend fun addComment(userId: Long, postId: Long, content: String): PostCommentRow?

    //댓글 삭제
    suspend fun removeComment(commentId: Long, postId: Long): Boolean

    //댓글 찾기
    suspend fun findComment(commentId: Long, postId: Long): PostCommentRow?

    //댓글 목록 가져오기
    suspend fun getComments(postId: Long, pageNumber: Int, pageSize: Int): List<PostCommentRow>
}
