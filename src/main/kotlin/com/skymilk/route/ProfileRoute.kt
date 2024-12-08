package com.skymilk.route

import com.skymilk.model.PostResponse
import com.skymilk.model.ProfileResponse
import com.skymilk.model.UpdateUserParams
import com.skymilk.repository.profile.ProfileRepository
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
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Routing.profileRoute() {
    val repository by application.inject<ProfileRepository>()

    //인증 처리된 유저만 접근할 수 있다
    authenticate {

        //프로필 라우트
        route("/profile") {
            //프로필 정보 가져오기
            get("/{userId}") {
                try {
                    val userId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = Constants.CURRENT_USER_ID_PARAMETER, isQueryParameter = true)

                    //결과 리턴
                    val result = repository.getUserById(userId, currentUserId)
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

            //프로필 갱신
            post("/update") {
                var fileName = ""
                var params: UpdateUserParams? = null
                var multiPart = call.receiveMultipart()
                try {
                    //multiPart 처리
                    multiPart.forEachPart { partData ->
                        when (partData) {
                            //이미지라면 저장
                            is PartData.FileItem -> {
                                fileName = partData.saveFile(folderPath = Constants.PROFILE_IMAGES_FOLDER_PATH)
                            }

                            //작성 데이터라면 파라미터 역직렬화
                            is PartData.FormItem -> {
                                if (partData.name == "profile_data") {
                                    params = Json.decodeFromString(partData.value)
                                }
                            }

                            else -> Unit
                        }

                        //사용된 PartData 처리
                        partData.dispose()
                    }

                    //저장된 이미지 경로 설정
                    val imageUrl = "${Constants.BASE_URL}${Constants.PROFILE_IMAGES_FOLDER}$fileName"


                    //프로필 갱신
                    val result = repository.updateUser(
                        params = params!!.copy(
                            //저장된 이미지가 없다면 기존 값 반영
                            imageUrl = if (fileName.isEmpty()) params!!.imageUrl else imageUrl
                        )
                    )
                    call.respond(status = result.code, message = result.data)
                } catch (e: Throwable) {
                    deleteFile("${Constants.PROFILE_IMAGES_FOLDER_PATH}$/$fileName")

                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = ProfileResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }
        }
    }
}
