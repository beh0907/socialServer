package com.skymilk.route

import com.skymilk.model.AuthResponse
import com.skymilk.model.SignInParams
import com.skymilk.model.SignUpParams
import com.skymilk.repository.auth.AuthRepository
import com.skymilk.util.Constants
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Routing.authRoute() {
    val repository by application.inject<AuthRepository>()


    route("/signUp") {
        post {
            val params = call.receiveNullable<SignUpParams>()

            //파라미터가 다르다면
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                )

                return@post
            }

            //유저 정보 리턴
            val result = repository.signUp(params = params)
            call.respond(result.code, result.data)
        }
    }

    route("/signIn") {
        post {
            val params = call.receiveNullable<SignInParams>()

            //파라미터가 다르다면
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                )

                return@post
            }

            //유저 정보 리턴
            val result = repository.signIn(params)
            call.respond(result.code, result.data)
        }
    }
}
