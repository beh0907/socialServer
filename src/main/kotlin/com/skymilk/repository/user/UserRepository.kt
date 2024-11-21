package com.skymilk.repository.user

import com.skymilk.model.AuthResponse
import com.skymilk.model.SignInParams
import com.skymilk.model.SignUpParams
import com.skymilk.util.Response

interface UserRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}
