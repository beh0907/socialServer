package com.skymilk.route

import com.skymilk.model.AuthResponse
import com.skymilk.model.FollowsParams
import com.skymilk.model.FollowsResponse
import com.skymilk.repository.follows.FollowsRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Routing.followsRoute() {
    val repository by application.inject<FollowsRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {
        route("/follow") {
            post {
                val params = call.receiveNullable<FollowsParams>()

                //파라미터가 다르다면
                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowsResponse(
                            success = false,
                            message = "유효하지 않은 설정 정보입니다."
                        )
                    )

                    return@post
                }

                //팔로우 처리
                val result = if (params.isFollowing) repository.followUser(params = params)
                else repository.unFollowUser(params)

                //결과 리턴
                call.respond(result.code, result.data)
            }
        }
    }
}
