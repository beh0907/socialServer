package com.skymilk.dao

import com.skymilk.dao.follows.FollowsTable
import com.skymilk.dao.post.PostTable
import com.skymilk.dao.postComments.PostCommentsTable
import com.skymilk.dao.postLikes.PostLikesTable
import com.skymilk.dao.user.UserTable
import com.skymilk.util.GlobalEnvironment.getProperty
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

private val postgresDriver = getProperty("postgres.driver")
private val postgresUrl = getProperty("postgres.url")
private val postgresUser = getProperty("postgres.user")
private val postgresPassword = getProperty("postgres.password")

object DatabaseFactory {

    //DB 연결 및 테이블 생성
    fun init() {
        Database.connect(createHikariCPDataSource())
        transaction {
            SchemaUtils.create(
                UserTable,
                FollowsTable,
                PostTable,
                PostLikesTable,
                PostCommentsTable
            )
        }
    }

    //DB 연결
    private fun createHikariCPDataSource(): HikariDataSource {
        val driverClass = postgresDriver
        val url = postgresUrl

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driverClass
            username = postgresUser
            password = postgresPassword

            maximumPoolSize = 3
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            validate()
        }

        return HikariDataSource(hikariConfig)
    }

    //Dispatchers.IO 쿼리 동작
    suspend fun <T> dbQuery(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
}
