package com.skymilk

import com.skymilk.dao.DatabaseFactory
import com.skymilk.di.configureDI
import com.skymilk.plugin.configureRouting
import com.skymilk.plugin.configureSecurity
import com.skymilk.plugin.configureSerialization
import com.skymilk.util.GlobalEnvironment
import com.skymilk.util.IdGenerator.generateId
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    GlobalEnvironment.init(environment)
    DatabaseFactory.init()

    configureSerialization()
    configureDI()
    configureSecurity()
    configureRouting()

    generateId()
}
