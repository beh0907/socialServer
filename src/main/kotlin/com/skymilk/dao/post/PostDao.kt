package com.skymilk.dao.post

interface PostDao {

    //게시글 작성
    suspend fun createPost(caption: String, imageUrl: String, userId: Long): Boolean

    //팔로워 게시글 목록 가져오기
    suspend fun getFeedsPost(userId: Long, follows: List<Long>, pageNumber: Int, pageSize: Int): List<PostRow>

    //특정 유저 게시글 목록 가져오기
    suspend fun getPostsByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow>

    //게시글 가져오기
    suspend fun getPost(postId: Long): PostRow?

    //게시글 가져오기
    suspend fun deletePost(postId: Long): Boolean

}
