package com.skymilk.dao.postLikes

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.util.IdGenerator
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class PostLikesDaoImpl : PostLikesDao {
    override suspend fun addLike(userId: Long, postId: Long): Boolean {
        return dbQuery {
            val insertStatement = PostLikesTable.insert {
                it[likeId] = IdGenerator.generateId()
                it[PostLikesTable.userId] = userId
                it[PostLikesTable.postId] = postId
            }

            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun removeLike(userId: Long, postId: Long): Boolean {
        return dbQuery {
            PostLikesTable.deleteWhere {
                (PostLikesTable.userId eq userId) and (PostLikesTable.postId eq postId)
            } > 0
        }
    }

    override suspend fun isPostLikeByUser(userId: Long, postId: Long): Boolean {
        return dbQuery {
            PostLikesTable.selectAll()
                .where { (PostLikesTable.userId eq userId) and (PostLikesTable.postId eq postId) }
                .toList()
                .isNotEmpty()
        }
    }
}
