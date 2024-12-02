package com.skymilk.repository.postLikes

import com.skymilk.dao.post.PostDao
import com.skymilk.dao.postLikes.PostLikesDao
import com.skymilk.model.PostParam
import com.skymilk.model.PostResponse
import com.skymilk.model.PostsResponse
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class PostLikesRepositoryImpl(
    private val postLikesDao: PostLikesDao,
) : PostLikesRepository {
    override suspend fun addLike(userId: Long, postId: Long): Boolean {
        return postLikesDao.addLike(userId, postId)
    }

    override suspend fun removeLike(userId: Long, postId: Long): Boolean {
        return postLikesDao.removeLike(userId, postId)
    }
}
