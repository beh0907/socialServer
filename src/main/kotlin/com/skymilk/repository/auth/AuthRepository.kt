package com.skymilk.repository.auth

import com.skymilk.model.AuthResponse
import com.skymilk.model.SignInParams
import com.skymilk.model.SignUpParams
import com.skymilk.util.Response

interface AuthRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}
