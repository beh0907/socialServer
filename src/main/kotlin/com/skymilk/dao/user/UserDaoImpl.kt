package com.skymilk.dao.user

import com.skymilk.dao.DatabaseFactory.dbQuery
import com.skymilk.model.SignUpParams
import com.skymilk.model.UserRow
import com.skymilk.model.UserTable
import com.skymilk.security.hashPassword
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword(params.password)
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.selectAll()
                .where { UserTable.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow): UserRow {
        return UserRow(
            seq = row[UserTable.seq],
            name = row[UserTable.name],
            email = row[UserTable.email],
            bio = row[UserTable.bio],
            password = row[UserTable.password],
            avatar = row[UserTable.avatar]
        )
    }
}
