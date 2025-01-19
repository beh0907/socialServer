package com.skymilk.dao.follows

import com.skymilk.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object FollowsTable: Table(name = "follows") {
    val followerId = long(name = "follower_id").autoIncrement().references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val followingId = long(name = "following_id").autoIncrement().references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val followDate = datetime(name = "follow_date").defaultExpression(defaultValue = CurrentDateTime)
}

