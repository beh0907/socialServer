package com.skymilk.dao.postComments

import com.skymilk.dao.post.PostTable
import com.skymilk.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PostCommentsTable : Table(name = "post_comments") {
    val commentId = long(name = "comment_id").uniqueIndex()
    val postId = long(name = "post_id").references(ref = PostTable.postId, onDelete = ReferenceOption.CASCADE)
    val userId = long(name = "user_id").references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val content = varchar(name = "content", length = 300)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

data class PostCommentRow(
    val commentId: Long,
    val postId: Long,
    val userId: Long,
    val userName: String,
    val userImageFileName: String?,
    val content: String,
    val createdAt: String,
)
