package com.skymilk.util

import io.ktor.server.application.ApplicationEnvironment

object GlobalEnvironment {
    lateinit var environment: ApplicationEnvironment

    fun init(environment: ApplicationEnvironment) {
        this.environment = environment
    }

    fun getProperty(name: String): String = environment.config.property(name).getString()
}
