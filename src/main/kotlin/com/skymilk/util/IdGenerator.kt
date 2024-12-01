package com.skymilk.util

import com.skymilk.util.GlobalEnvironment.environment
import de.mkammerer.snowflakeid.SnowflakeIdGenerator

private val generatorId = environment.config.property("snowflake.id.generator").getString()

object IdGenerator {

    fun generateId(): Long = SnowflakeIdGenerator.createDefault(generatorId.toInt()).next()

}
