package com.skymilk.di

import com.skymilk.dao.user.UserDao
import com.skymilk.dao.user.UserDaoImpl
import com.skymilk.repository.user.UserRepository
import com.skymilk.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
}
