package com.skymilk.dao.follows

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object FollowsTable: Table(name = "follows") {
    val followerId = long(name = "follower_id").autoIncrement()
    val followingId = long(name = "following_id").autoIncrement()
    val followDate = datetime(name = "follow_date").defaultExpression(defaultValue = CurrentDateTime)
}

