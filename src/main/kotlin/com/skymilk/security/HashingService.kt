package com.skymilk.security

import com.skymilk.util.GlobalEnvironment.getProperty
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val ALGORITHM = getProperty("hash.algorithm")
private val HASH_KEY = getProperty("hash.key").toByteArray()
private val hMacKey = SecretKeySpec(HASH_KEY, ALGORITHM)

fun hashPassword(password: String): String {
    val hMac = Mac.getInstance(ALGORITHM)
    hMac.init(hMacKey)

    return hex(hMac.doFinal(password.toByteArray()))
}
