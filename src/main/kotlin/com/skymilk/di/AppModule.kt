package com.skymilk.di

import com.skymilk.dao.follows.FollowsDao
import com.skymilk.dao.follows.FollowsDaoImpl
import com.skymilk.dao.user.UserDao
import com.skymilk.dao.user.UserDaoImpl
import com.skymilk.repository.auth.AuthRepository
import com.skymilk.repository.auth.AuthRepositoryImpl
import com.skymilk.repository.follows.FollowsRepository
import com.skymilk.repository.follows.FollowsRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    //인증
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }

    //팔로우
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) }
    single<FollowsDao> { FollowsDaoImpl () }
}
