package com.skymilk.route

import com.skymilk.model.FollowResponse
import com.skymilk.model.FollowsParams
import com.skymilk.repository.follows.FollowsRepository
import com.skymilk.util.Constants
import com.skymilk.util.getLongParameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Routing.followsRoute() {
    val repository by application.inject<FollowsRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {

        //팔로우
        route("/follow") {
            post {
                try {
                    val params = call.receiveNullable<FollowsParams>()

                    //파라미터가 다르다면
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = FollowResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )

                        return@post
                    }

                    //팔로우 처리
                    val result = repository.followUser(params = params)

                    //결과 리턴
                    call.respond(result.code, result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = FollowResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }
        }

        //언 팔로우
        route("/unfollow") {
            post {
                try {
                    val params = call.receiveNullable<FollowsParams>()

                    //파라미터가 다르다면
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = FollowResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )

                        return@post
                    }

                    //언팔로우 처리
                    val result = repository.unFollowUser(params = params)

                    //결과 리턴
                    call.respond(result.code, result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = FollowResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }
        }

        //팔로워 목록
        get(path = "/followers") {
            try {
                val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                val page = call.request.queryParameters[Constants.PAGE_NUMBER_PARAMETER]?.toIntOrNull() ?: 0
                val limit = call.request.queryParameters[Constants.PAGE_LIMIT_PARAMETER]?.toIntOrNull()
                    ?: Constants.DEFAULT_PAGINATION_PAGE_SIZE

                //팔로워 목록 리턴
                val result = repository.getFollowers(userId = userId, pageNumber = page, pageSize = limit)
                call.respond(
                    status = result.code,
                    message = result.data
                )

            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                )
            } catch (e: Throwable) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = Constants.UNEXPECTED_ERROR_MESSAGE
                )
            }
        }

        //팔로윙 목록
        get(path = "/following") {
            try {
                val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                val page = call.request.queryParameters[Constants.PAGE_NUMBER_PARAMETER]?.toIntOrNull() ?: 0
                val limit = call.request.queryParameters[Constants.PAGE_LIMIT_PARAMETER]?.toIntOrNull()
                    ?: Constants.DEFAULT_PAGINATION_PAGE_SIZE

                val result = repository.getFollowing(userId = userId, pageNumber = page, pageSize = limit)
                call.respond(
                    status = result.code,
                    message = result.data
                )
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                )
            } catch (e: Throwable) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = Constants.UNEXPECTED_ERROR_MESSAGE
                )
            }
        }

        //추천 목록
        get(path = "/suggestions") {
            try {
                val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                val result = repository.getFollowingSuggestions(userId = userId)
                call.respond(
                    status = result.code,
                    message = result.data
                )
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                )
            } catch (e: Throwable) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = Constants.UNEXPECTED_ERROR_MESSAGE
                )
            }
        }

    }
}
