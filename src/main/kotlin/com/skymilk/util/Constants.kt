package com.skymilk.util

object Constants {
    const val BASE_URL = "http://192.168.0.10:8888/"

    //게시글 이미지 저장 경로
    const val POST_IMAGES_FOLDER = "post_images/"
    const val POST_IMAGES_FOLDER_PATH = "build/resources/main/static/$POST_IMAGES_FOLDER"

    //프로필 이미지 저장 경로
    const val PROFILE_IMAGES_FOLDER = "profile_images/"
    const val PROFILE_IMAGES_FOLDER_PATH = "build/resources/main/static/$PROFILE_IMAGES_FOLDER"

    // 페이지당 기본 20개씩
    const val DEFAULT_PAGINATION_PAGE_SIZE = 20

    // 추천 유저 목록 10씩
    const val SUGGESTED_FOLLOWING_LIMIT = 10

    //오류 메시지
    const val UNEXPECTED_ERROR_MESSAGE = "예상치 못한 오류가 발생했습니다. 다시 시도해 주세요!"
    const val MISSING_PARAMETERS_ERROR_MESSAGE = "유효하지 않은 정보입니다."

    //파라미터 아이디
    const val PAGE_NUMBER_PARAMETER = "page"
    const val PAGE_LIMIT_PARAMETER = "limit"
    const val USER_ID_PARAMETER = "userId"
    const val CURRENT_USER_ID_PARAMETER = "currentUserId"
}
