package com.skymilk.di

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.follows.FollowsDaoImpl
import com.skymilk.dao.post.PostDao
import com.skymilk.dao.post.PostDaoImpl
import com.skymilk.dao.postComments.PostCommentsDao
import com.skymilk.dao.postComments.PostCommentsDaoImpl
import com.skymilk.dao.postLikes.PostLikesDao
import com.skymilk.dao.postLikes.PostLikesDaoImpl
import com.skymilk.dao.user.UserDao
import com.skymilk.dao.user.UserDaoImpl
import com.skymilk.repository.auth.AuthRepository
import com.skymilk.repository.auth.AuthRepositoryImpl
import com.skymilk.repository.follows.FollowsRepository
import com.skymilk.repository.follows.FollowsRepositoryImpl
import com.skymilk.repository.post.PostRepository
import com.skymilk.repository.post.PostRepositoryImpl
import com.skymilk.repository.postComments.PostCommentsRepository
import com.skymilk.repository.postComments.PostCommentsRepositoryImpl
import com.skymilk.repository.postLikes.PostLikesRepository
import com.skymilk.repository.postLikes.PostLikesRepositoryImpl
import com.skymilk.repository.profile.ProfileRepository
import com.skymilk.repository.profile.ProfileRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    //인증
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }

    //프로필 처리
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    //팔로우
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) }
    single<FollowsDao> { FollowsDaoImpl() }

    //게시글
    single<PostRepository> { PostRepositoryImpl(get(), get(), get()) }
    single<PostDao> { PostDaoImpl() }

    //게시글 좋아요
    single<PostLikesRepository> { PostLikesRepositoryImpl(get(), get()) }
    single<PostLikesDao> { PostLikesDaoImpl() }

    //댓글
    single<PostCommentsRepository> { PostCommentsRepositoryImpl(get(), get()) }
    single<PostCommentsDao> { PostCommentsDaoImpl() }
}
