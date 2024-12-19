package com.skymilk.route

import com.skymilk.model.AddCommentParams
import com.skymilk.model.CommentResponse
import com.skymilk.model.CommentsResponse
import com.skymilk.model.RemoveCommentParams
import com.skymilk.repository.postComments.PostCommentsRepository
import com.skymilk.util.Constants
import com.skymilk.util.getLongParameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Routing.commentRoute() {
    val repository by application.inject<PostCommentsRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {

        route(path = "/post/comments") {
            //댓글 추가
            post(path = "/create") {
                try {
                    val params = call.receiveNullable<AddCommentParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )
                        return@post
                    }

                    val result = repository.addComment(params = params)
                    println(1111111111)
                    println(result.code)
                    println(result.data)
                    call.respond(status = result.code, message = result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }

            //댓글 삭제
            delete(path = "/delete") {
                try {
                    val params = call.receiveNullable<RemoveCommentParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )
                        return@delete
                    }

                    val result = repository.removeComment(params = params)
                    println(44444444444444)
                    println(result.code)
                    println(result.data)
                    call.respond(status = result.code, message = result.data)
                } catch (error: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도 해주세요."
                        )
                    )
                }
            }


            //게시글의 댓글 목록 가져오기
            get(path = "/{postId}") {
                try {
                    val postId = call.getLongParameter(name = "postId")
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGINATION_PAGE_SIZE

                    val result = repository.getComments(postId = postId, pageNumber = page, pageSize = limit)
                    call.respond(status = result.code, message = result.data)
                } catch (e: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentsResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }
        }

    }
}
