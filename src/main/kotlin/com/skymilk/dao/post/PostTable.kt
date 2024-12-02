package com.skymilk.dao.post

import com.skymilk.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PostTable: Table(name = "posts") {
    val postId = long(name = "post_id").autoIncrement()
    val caption = varchar(name = "caption", length = 300)
    val imageUrl = varchar(name = "image_url", length = 300)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
    val likesCount = integer(name = "likes_count").default(0)
    val commentsCount = integer(name = "comments_count").default(0)
    val userId = long(name = "user_id").references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE) // 유저 정보가 삭제될 때 함께 삭제
}

data class PostRow(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
)
