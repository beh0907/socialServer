package com.skymilk.route

import com.skymilk.model.PostParam
import com.skymilk.model.PostResponse
import com.skymilk.model.PostUpdateParam
import com.skymilk.model.PostsResponse
import com.skymilk.repository.post.PostRepository
import com.skymilk.util.Constants
import com.skymilk.util.deleteFile
import com.skymilk.util.getLongParameter
import com.skymilk.util.saveFile
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Routing.postRoute() {
    val repository by application.inject<PostRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {

        //단일 게시물에 대한 라우트
        route("/post") {
            //게시글 생성 라우트
            post("/create") {
                var fileName = ""
                var params: PostParam? = null
                var multiPart = call.receiveMultipart()

                //multiPart 처리
                multiPart.forEachPart { partData ->
                    when (partData) {
                        //이미지라면 저장
                        is PartData.FileItem -> {
                            fileName = partData.saveFile(folderPath = Constants.POST_IMAGES_FOLDER_PATH)
                        }

                        //작성 데이터라면 파라미터 역직렬화
                        is PartData.FormItem -> {
                            if (partData.name == "post_data") {
                                params = Json.decodeFromString(partData.value)
                            }
                        }

                        else -> Unit
                    }

                    //사용된 PartData 처리
                    partData.dispose()
                }

                //저장된 이미지 경로 설정
                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                //파라미터를 확인할 수 없다면 저장된 이미지 제거
                if (params == null) {
                    deleteFile("${Constants.POST_IMAGES_FOLDER_PATH}$/$fileName")

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = PostResponse(
                            success = false,
                            message = "입력된 게시물 정보를 확인할 수 없습니다."
                        )
                    )

                    return@post
                } else {
                    //게시물 생성 후 리턴
                    val result = repository.createPost(imageUrl, params!!)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }
            }

            //게시글 수정 라우트
            post("/update") {
                var fileName = ""
                var params: PostUpdateParam? = null
                var multiPart = call.receiveMultipart()

                //multiPart 처리
                multiPart.forEachPart { partData ->
                    when (partData) {
                        //이미지라면 저장
                        is PartData.FileItem -> {
                            fileName = partData.saveFile(folderPath = Constants.POST_IMAGES_FOLDER_PATH)
                            println("FileItem : $fileName")
                        }

                        //작성 데이터라면 파라미터 역직렬화
                        is PartData.FormItem -> {
                            if (partData.name == "post_data") {
                                params = Json.decodeFromString(partData.value)
                                println("post_data : $params")
                            }
                        }

                        else -> Unit
                    }

                    //사용된 PartData 처리
                    partData.dispose()
                }

                //저장된 이미지 경로 설정
                //선택된 이미지가 없다면 null을 전달해 이미지 경로를 갱신하지 않는다
                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                //파라미터를 확인할 수 없다면 저장된 이미지 제거
                if (params == null) {
                    deleteFile("${Constants.POST_IMAGES_FOLDER_PATH}$/$fileName")

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = PostResponse(
                            success = false,
                            message = "입력된 게시물 정보를 확인할 수 없습니다."
                        )
                    )

                    return@post
                } else {
                    //게시물 갱신 후 리턴
                    val result = repository.updatePost(imageUrl = if (fileName.isBlank()) params!!.imageUrl else imageUrl, params = params!!)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }
            }

            //게시글 조회 라우트
            get("/{postId}") {
                try {
                    //파라미터 확인
                    val postId = call.getLongParameter(name = "postId")
                    val currentUserId =
                        call.getLongParameter(name = Constants.CURRENT_USER_ID_PARAMETER, isQueryParameter = true)

                    //게시글 가져오기
                    val result = repository.getPost(postId, currentUserId)
                    call.respond(status = result.code, message = result.data)
                } catch (e: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (t: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도해 주세요."
                        )
                    )
                }
            }

            //게시글 삭제 라우트
            delete("/{postId}") {
                try {
                    //파라미터 확인
                    val postId = call.getLongParameter(name = "postId")

                    //게시글 삭제하기
                    val result = repository.deletePost(postId)

                    println("result code : ${result.code}")
                    println("result data : ${result.data}")
                    call.respond(status = result.code, message = result.data)
                } catch (e: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (t: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도해 주세요."
                        )
                    )
                }
            }
        }

        //복수 게시물에 대한 라우트
        route("/posts") {

            //피드 목록
            get("/feed") {
                try {
                    val currentUserId =
                        call.getLongParameter(name = Constants.CURRENT_USER_ID_PARAMETER, isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit =
                        call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGINATION_PAGE_SIZE

                    val result = repository.getFeedsPost(currentUserId, page, limit)
                    call.respond(status = result.code, message = result.data)
                } catch (e: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (t: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도해 주세요."
                        )
                    )
                }
            }

            //특정 유저의 게시글 목록
            get("/{userId}") {
                try {
                    val postsOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId =
                        call.getLongParameter(name = Constants.CURRENT_USER_ID_PARAMETER, isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit =
                        call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGINATION_PAGE_SIZE

                    val result = repository.getPostsByUser(postsOwnerId, currentUserId, page, limit)
                    call.respond(status = result.code, message = result.data)
                } catch (e: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (t: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "오류가 발생하였습니다. 다시 시도해 주세요."
                        )
                    )
                }
            }
        }
    }
}
