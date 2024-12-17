package com.skymilk.dao.postComments

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.dao.user.UserTable
import com.skymilk.util.IdGenerator
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class PostCommentsDaoImpl : PostCommentsDao {

    override suspend fun addComment(
        userId: Long,
        postId: Long,
        content: String,
    ): PostCommentRow? {
        return dbQuery {
            //댓글 삽입
            val commentId = IdGenerator.generateId()
            PostCommentsTable.insert {
                it[PostCommentsTable.commentId] = commentId
                it[PostCommentsTable.userId] = userId
                it[PostCommentsTable.postId] = postId
                it[PostCommentsTable.content] = content
            }

            //삽입된 댓글 리턴
            findComment(commentId, postId)
        }
    }

    override suspend fun removeComment(commentId: Long, postId: Long): Boolean {
        return dbQuery {
            PostCommentsTable.deleteWhere {
                (PostCommentsTable.commentId eq commentId) and (PostCommentsTable.postId eq postId)
            } > 0
        }
    }

    override suspend fun findComment(
        commentId: Long,
        postId: Long,
    ): PostCommentRow? {
        val result = getJoinUserToPostCommentsTable().selectAll()
            .where { (PostCommentsTable.commentId eq commentId) and (PostCommentsTable.postId eq postId) }
            .singleOrNull()
            ?.let {
                toPostCommentRow(it)
            }

        return result
    }

    override suspend fun getComments(
        postId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): List<PostCommentRow> {
        return dbQuery {
            getJoinUserToPostCommentsTable().selectAll()
                .where { PostCommentsTable.postId eq postId }
                .orderBy(PostCommentsTable.createdAt, SortOrder.DESC)
                .limit(pageSize)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .map { toPostCommentRow(it) }
        }
    }

    //유저와 댓글 테이블 조인
    private fun getJoinUserToPostCommentsTable(): Join {
        return PostCommentsTable.join(
            otherTable = UserTable,
            onColumn = PostCommentsTable.userId,
            otherColumn = UserTable.id,
            joinType = JoinType.INNER // Inner Join 설정
        )
    }

    private fun toPostCommentRow(row: ResultRow): PostCommentRow {
        return PostCommentRow(
            commentId = row[PostCommentsTable.commentId],
            content = row[PostCommentsTable.content],
            createdAt = row[PostCommentsTable.createdAt].toString(),
            userId = row[PostCommentsTable.userId],
            userName = row[UserTable.name],
            userImageUrl = row[UserTable.imageUrl],
            postId = row[PostCommentsTable.postId],
        )
    }
}
