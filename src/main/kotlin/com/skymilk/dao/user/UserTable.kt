package com.skymilk.dao.user

import org.jetbrains.exposed.sql.Table

object UserTable: Table(name = "users") {
    val id = long(name = "user_id").autoIncrement()
    val name = varchar("user_name", 255)
    val email = varchar("user_email", 255)
    val bio = text("user_bio").default("안녕하십니까! 저의 소셜 페이지에 오신 것을 환영합니다.")
    val password = varchar("user_password", 100)
    val fileName = varchar("file_name", 50).nullable()
    val followersCount = integer("followers_count").default(0)
    val followingCount = integer("following_count").default(0)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

data class UserRow(
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val fileName: String?,
    val password: String,
    val followersCount: Int,
    val followingCount: Int,
)
