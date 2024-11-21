package com.skymilk.plugin

import com.skymilk.route.authRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoute()
    }
}
