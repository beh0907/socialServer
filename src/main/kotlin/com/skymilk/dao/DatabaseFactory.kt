package com.skymilk.dao

import com.skymilk.model.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    //DB 연결 및 테이블 생성
    fun init() {
        Database.connect(createHikariCPDataSource())
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    //DB 연결
    private fun createHikariCPDataSource(): HikariDataSource {
        val driverClass = "org.postgresql.Driver"
        val url = "jdbc:postgresql://localhost:5432/socialDB"

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driverClass
            username = "postgres"
            password = "!vnf04080907"

            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
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
