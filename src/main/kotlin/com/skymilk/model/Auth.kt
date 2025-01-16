package com.skymilk.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpParams(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class SignInParams(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val data: Auth? = null,
    val message: String? = null
)

@Serializable
data class Auth(
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val fileName: String? = null,
    val token: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
)
