package com.skymilk.repository.user

import com.skymilk.dao.user.UserDao
import com.skymilk.model.AuthResponse
import com.skymilk.model.AuthResponseData
import com.skymilk.model.SignInParams
import com.skymilk.model.SignUpParams
import com.skymilk.plugin.generateToken
import com.skymilk.security.hashPassword
import com.skymilk.util.Response
import io.ktor.http.HttpStatusCode

class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {
    //회원가입
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        println("1")
        return if (userAlreadyExist(params.email)) {
            println("2")
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = "이미 가입된 이메일입니다."
                )
            )
        } else {
            println("3")
            val insertUser = userDao.insert(params)

            println("4")
            if (insertUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        errorMessage = "계정 생성을 실패하였습니다. 다시 시도 해주세요."
                    )
                )
            } else {
                println("5")
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            seq = insertUser.seq,
                            name = insertUser.name,
                            email = insertUser.email,
                            bio = insertUser.bio,
                            avatar = insertUser.avatar,
                            token = generateToken(params.email)
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
                    errorMessage = "등록되지 않은 이메일입니다."
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            if (user.password != hashedPassword) {
                Response.Error(
                    code = HttpStatusCode.NotFound,
                    data = AuthResponse(
                        errorMessage = "비밀번호가 일치하지 않습니다."
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            seq = user.seq,
                            name = user.name,
                            email = user.email,
                            bio = user.bio,
                            avatar = user.avatar,
                            token = generateToken(params.email)
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
