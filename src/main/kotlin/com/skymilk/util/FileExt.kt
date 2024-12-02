package com.skymilk.util

import io.ktor.http.content.PartData
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.util.UUID

fun PartData.FileItem.saveFile(folderPath: String): String {
    //파일 정보 설정
    val fileName = "${UUID.randomUUID()}.${File(originalFileName as String).extension}"
    val fileBytes = provider().toInputStream().readBytes()

    //폴더 생성
    val folder = File(folderPath)
    folder.mkdirs()

    //파일 저장
    folder.resolve(fileName).writeBytes(fileBytes)

    return fileName
}

fun deleteFile(filePath: String): Boolean = File(filePath).delete()
