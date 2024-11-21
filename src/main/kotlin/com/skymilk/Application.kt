package com.skymilk

import com.skymilk.dao.DatabaseFactory
import com.skymilk.di.configureDI
import com.skymilk.plugin.configureRouting
import com.skymilk.plugin.configureSecurity
import com.skymilk.plugin.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureDI()
    configureSecurity()
    configureRouting()
}
