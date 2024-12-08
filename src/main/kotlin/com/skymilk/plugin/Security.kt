package com.skymilk.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.skymilk.dao.user.UserDao
import com.skymilk.model.AuthResponse
import com.skymilk.util.GlobalEnvironment.getProperty
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

private val jwtSecret = getProperty("jwt.secret")
private val jwtIssuer = getProperty("jwt.issuer")
private val jwtAudience = getProperty("jwt.audience")
private val jwtRealm = getProperty("jwt.realm")

private const val CLAIM = "email"

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )

            validate { credential ->
                val email = credential.payload.getClaim(CLAIM).asString()
                if (email != null) {
                    // 토큰 만료 여부 확인
//                    val isTokenExpired = credential.payload.expiresAt?.let { it.time < System.currentTimeMillis() } ?: false

                    //토큰 페이로드 내 이메일이 DB에 저장 여부 체크
                    val userExists = userDao.findByEmail(email = email) != null

                    //토큰 페이로드 내 audience값이 일치 여부 체크
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)

                    //정상 토큰 여부 확인
                    if (userExists && isValidAudience /*&& !isTokenExpired*/) {
                        JWTPrincipal(payload = credential.payload)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    message = AuthResponse(message = "토큰이 유효하지 않거나 만료되었습니다.")
                )
            }
        }
    }
}

fun generateToken(email: String): String {
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(CLAIM, email)
//        .withExpiresAt(Date.from(Instant.now().plusSeconds(24 * 60 * 60))) // 24시간 후 만료
        .sign(Algorithm.HMAC256(jwtSecret))

}
