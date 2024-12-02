package com.skymilk.util

import com.skymilk.model.PostResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond

//요청 파라미터 내 필드를 체크해 Long 반환
suspend fun ApplicationCall.getLongParameter(name: String, isQueryParameter: Boolean = false): Long {
    val param = if (isQueryParameter) {
        request.queryParameters[name]?.toLongOrNull()
    } else {
        parameters[name]?.toLongOrNull()
    } ?: run {
        respond(
            status = HttpStatusCode.BadRequest,
            message = PostResponse(
                success = false,
                message = "${name}을 확인할 수 없습니다."
            )
        )

        throw BadRequestException("${name}을 확인할 수 없습니다.")
    }

    return param
}
