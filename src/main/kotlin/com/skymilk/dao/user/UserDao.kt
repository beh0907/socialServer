package com.skymilk.dao.user

import com.skymilk.model.SignUpParams
import com.skymilk.model.UserRow

interface UserDao {
    suspend fun insert(params: SignUpParams): UserRow?

    suspend fun findByEmail(email: String): UserRow?
}
