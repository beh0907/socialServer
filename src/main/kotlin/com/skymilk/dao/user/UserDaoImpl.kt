package com.skymilk.dao.user

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.model.SignUpParams
import com.skymilk.security.hashPassword
import com.skymilk.util.IdGenerator
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

class UserDaoImpl : UserDao {

    //유저 정보 추가
    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[id] = IdGenerator.generateId()
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword(params.password)
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    //이메일 기준 유저 정보 검색
    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.selectAll()
                .where { UserTable.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    //팔로우 여부에 따라 카운트 수정
    override suspend fun updateFollowsCount(
        follower: Long,
        following: Long,
        isFollower: Boolean,
    ): Boolean {
        return dbQuery {

            val count = if (isFollower) 1 else -1

            val followerQuery = UserTable.update({UserTable.id eq follower}) {
                it.update(column = followingCount, value = followingCount.plus(count))
            } > 0

            val followingQuery = UserTable.update({UserTable.id eq following}) {
                it.update(column = followersCount, value = followersCount.plus(count))
            } > 0

            followerQuery && followingQuery
        }
    }

    private fun rowToUser(row: ResultRow): UserRow {
        return UserRow(
            id = row[UserTable.id],
            name = row[UserTable.name],
            email = row[UserTable.email],
            bio = row[UserTable.bio],
            password = row[UserTable.password],
            imageUrl = row[UserTable.imageUrl],
            followersCount = row[UserTable.followersCount],
            followingCount = row[UserTable.followingCount],
        )
    }
}
