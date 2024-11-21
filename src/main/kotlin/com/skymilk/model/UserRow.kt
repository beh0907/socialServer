package com.skymilk.model

import org.jetbrains.exposed.sql.Table

object UserTable: Table(name = "users") {
    val seq = integer(name = "user_seq").autoIncrement()
    val name = varchar("user_name", 255)
    val email = varchar("user_email", 255)
    val bio = text("user_bio").default("안녕하십니까! SocialApp에 오신 것을 환영합니다.")
    val password = varchar("user_password", 100)
    val avatar = text("user_avatar").nullable()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(seq)
}

data class UserRow(
    val seq: Int,
    val name: String,
    val email: String,
    val bio: String,
    val avatar: String?,
    val password: String
)
