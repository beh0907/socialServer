package com.skymilk.repository.auth

import com.skymilk.dao.user.UserDao
import com.skymilk.model.Auth
import com.skymilk.model.AuthResponse
import com.skymilk.model.SignInParams
import com.skymilk.model.SignUpParams
import com.skymilk.plugin.generateToken
import com.skymilk.security.hashPassword
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class AuthRepositoryImpl(
    private val userDao: UserDao,
) : AuthRepository {
    //회원가입
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    message = "이미 가입된 이메일입니다."
                )
            )
        } else {
            val insertUser = userDao.insert(params.name, params.email, params.password)

            if (insertUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        message = "계정 생성을 실패하였습니다. 다시 시도 해주세요."
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = Auth(
                            id = insertUser.id,
                            name = insertUser.name,
                            email = insertUser.email,
                            bio = insertUser.bio,
                            fileName = insertUser.fileName,
                            token = generateToken(params.email),
                            followingCount = insertUser.followingCount,
                            followersCount = insertUser.followersCount
                        )
                    )
                )
            }
        }
    }

    //로그인
    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    message = "등록되지 않은 이메일입니다."
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            if (user.password != hashedPassword) {
                Response.Error(
                    code = HttpStatusCode.NotFound,
                    data = AuthResponse(
                        message = "비밀번호가 일치하지 않습니다."
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = Auth(
                            id = user.id,
                            name = user.name,
                            email = user.email,
                            bio = user.bio,
                            fileName = user.fileName,
                            token = generateToken(params.email),
                            followingCount = user.followingCount,
                            followersCount = user.followersCount
                        )
                    )
                )
            }
        }
    }

    //이메일 등록 여부 체크
    private suspend fun userAlreadyExist(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }
}
