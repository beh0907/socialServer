package com.skymilk.route

import com.skymilk.model.LikeParams
import com.skymilk.model.LikeResponse
import com.skymilk.repository.postLikes.PostLikesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Routing.postLikesRoute() {
    val repository by application.inject<PostLikesRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {
        route(path = "/post/likes") {

            //좋아요 설정
            post(path = "/add") {
                try {

                    val params = call.receiveNullable<LikeParams>()

                    //파라미터가 다르다면
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "유효하지 않은 설정 정보입니다."
                            )
                        )

                        return@post
                    }

                    //좋아요 설정 여부 리턴
                    val result = repository.addLike(params = params)
                    call.respond(result.code, result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도 해주세요."
                        )
                    )
                    return@post
                }
            }

            //좋아요 제거
            post(path = "/remove") {
                try {
                    val params = call.receiveNullable<LikeParams>()

                    //파라미터가 다르다면
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "유효하지 않은 설정 정보입니다."
                            )
                        )

                        return@post
                    }

                    //좋아요 제거 여부 리턴
                    val result = repository.removeLike(params = params)
                    call.respond(result.code, result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도 해주세요."
                        )
                    )
                    return@post
                }
            }

        }
    }
}
