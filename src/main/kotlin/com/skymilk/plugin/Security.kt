package com.skymilk.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.skymilk.model.AuthResponse
import com.skymilk.util.GlobalEnvironment.getProperty
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond

private val jwtSecret = getProperty("jwt.secret")
private val jwtIssuer = getProperty("jwt.issuer")
private val jwtAudience = getProperty("jwt.audience")
private val jwtRealm = getProperty("jwt.realm")

private const val CLAIM = "email"

fun Application.configureSecurity() {
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(CLAIM).asString() != null) JWTPrincipal(payload = credential.payload)
                else null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    message = AuthResponse(errorMessage = "토큰이 유효하지 않거나 만료되었습니다.")
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
//        .withExpiresAt()
        .sign(Algorithm.HMAC256(jwtSecret))

}
