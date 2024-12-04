package com.skymilk.dao.user

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.dao.user.UserTable.followersCount
import com.skymilk.security.hashPassword
import com.skymilk.util.IdGenerator
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserDaoImpl : UserDao {
    //유저 정보 추가
    override suspend fun insert(name: String, email: String, password: String): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[id] = IdGenerator.generateId()
                it[UserTable.name] = name
                it[UserTable.email] = email
                it[UserTable.password] = hashPassword(password)
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

    //아이디로 유저 정보 가져오기
    override suspend fun findById(userId: Long): UserRow? {
        return dbQuery {
            UserTable.selectAll()
                .where { UserTable.id eq userId }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    //유저 정보 갱신
    override suspend fun updateUser(
        userId: Long,
        name: String,
        bio: String,
        imageUrl: String?,
    ): Boolean {
        return dbQuery {
            UserTable.update({ UserTable.id eq userId }) {
                it[UserTable.name] = name
                it[UserTable.bio] = bio
                it[UserTable.imageUrl] = imageUrl
            } > 0
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

            val followerQuery = UserTable.update({ UserTable.id eq follower }) {
                it.update(column = followingCount, value = followingCount.plus(count))
            } > 0

            val followingQuery = UserTable.update({ UserTable.id eq following }) {
                it.update(column = followersCount, value = followersCount.plus(count))
            } > 0

            followerQuery && followingQuery
        }
    }

    //조건에 해당하는 유저 목록 가져오기
    override suspend fun getUsers(userIds: List<Long>): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .where { UserTable.id inList userIds }
                .map { rowToUser(it) }
        }
    }

    //인기순 유저 목록 가져오기
    override suspend fun getPopularUsers(limit: Int): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .orderBy(followersCount, SortOrder.DESC)
                .limit(limit)
                .map { rowToUser(it) }
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
