package com.skymilk.repository.profile

import com.skymilk.model.ProfileResponse
import com.skymilk.model.UpdateUserParams
import com.skymilk.util.Response

interface ProfileRepository {
    suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse>

    suspend fun updateUser(params: UpdateUserParams): Response<ProfileResponse>
}
