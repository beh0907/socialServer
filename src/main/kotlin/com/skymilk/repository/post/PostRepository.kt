package com.skymilk.repository.post

import com.skymilk.model.PostParam
import com.skymilk.model.PostResponse
import com.skymilk.model.PostsResponse
import com.skymilk.util.Response

interface PostRepository {

    //게시글 작성
    suspend fun createPost(imageUrl: String, params: PostParam): Response<PostResponse>

    //팔로워 게시글 목록 가져오기
    suspend fun getFeedsPost(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse>

    //특정 유저 게시글 목록 가져오기
    suspend fun getPostsByUser(
        postOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<PostsResponse>

    //게시글 가져오기
    suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse>

    //게시글 가져오기
    suspend fun deletePost(postId: Long): Response<PostResponse>

}
