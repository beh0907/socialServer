package com.skymilk.dao.post

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.dao.user.UserTable
import com.skymilk.util.IdGenerator
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class PostDaoImpl : PostDao {
    override suspend fun createPost(
        caption: String,
        imageUrl: String,
        userId: Long,
    ): PostRow? {
        return dbQuery {
            val postId = IdGenerator.generateId()

            val insertStatement = PostTable.insert {
                it[PostTable.postId] = postId
                it[PostTable.caption] = caption
                it[PostTable.imageUrl] = imageUrl
                it[likesCount] = 0
                it[commentsCount] = 0
                it[PostTable.userId] = userId
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                getJoinUserToPostTable().selectAll()
                    .where { PostTable.postId eq postId }
                    .singleOrNull()
                    ?.let { toPostRow(it) }
            }
        }
    }

    override suspend fun getFeedsPost(
        userId: Long,
        follows: List<Long>,
        pageNumber: Int,
        pageSize: Int,
    ): List<PostRow> {
        return dbQuery {
            if (follows.size > 1) {
                //팔로우를 했다면
                getJoinUserToPostTable().selectAll()
                    .where { PostTable.userId inList follows }
                    .orderBy(PostTable.createdAt, SortOrder.DESC)
                    .limit(pageSize)
                    .offset(((pageNumber - 1) * pageSize).toLong())
                    .map { toPostRow(it) }
            } else {
                //팔로우가 없다면 전체 인기순 게시글 표시
                getJoinUserToPostTable().selectAll()
                    .orderBy(PostTable.likesCount, SortOrder.DESC)
                    .limit(pageSize)
                    .offset(((pageNumber - 1) * pageSize).toLong())
                    .map { toPostRow(it) }
            }
        }
    }

    override suspend fun getPostsByUser(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): List<PostRow> {
        return dbQuery {
            getJoinUserToPostTable().selectAll()
                .where { PostTable.userId eq userId }
                .orderBy(PostTable.createdAt, SortOrder.DESC)
                .limit(pageSize)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .map { toPostRow(it) }
        }
    }

    override suspend fun getPost(postId: Long): PostRow? {
        return dbQuery {
            getJoinUserToPostTable().selectAll()
                .where { PostTable.postId eq postId }
                .singleOrNull()
                ?.let { toPostRow(it) }
        }
    }

    override suspend fun deletePost(postId: Long): Boolean {
        return dbQuery {
            PostTable.deleteWhere { PostTable.postId eq postId } > 0
        }
    }

    override suspend fun updateLikesCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(where = { PostTable.postId eq postId }) {
                it.update(column = likesCount, value = likesCount.plus(value))
            } > 0
        }
    }

    override suspend fun updateCommentsCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(where = { PostTable.postId eq postId }) {
                it.update(column = commentsCount, value = commentsCount.plus(value))
            } > 0
        }
    }

    //유저와 게시글 테이블 조인
    private fun getJoinUserToPostTable(): Join {
        return PostTable.join(
            otherTable = UserTable,
            onColumn = PostTable.userId,
            otherColumn = UserTable.id,
            joinType = JoinType.INNER // Inner Join 설정
        )
    }

    //조인 결과 변환
    private fun toPostRow(row: ResultRow): PostRow {
        return PostRow(
            postId = row[PostTable.postId],
            caption = row[PostTable.caption],
            imageUrl = row[PostTable.imageUrl],
            createdAt = row[PostTable.createdAt].toString(),
            likesCount = row[PostTable.likesCount],
            commentsCount = row[PostTable.commentsCount],
            userId = row[PostTable.userId],
            userName = row[UserTable.name],
            userImageUrl = row[UserTable.imageUrl],
        )
    }
}
