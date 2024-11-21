package com.skymilk.security

import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val ALGORITHM = "HmacSHA1"
private val HASH_KEY = "cryptoSecretSocial".toByteArray()
private val hMacKey = SecretKeySpec(HASH_KEY, ALGORITHM)

fun hashPassword(password: String): String {
    val hMac = Mac.getInstance(ALGORITHM)
    hMac.init(hMacKey)

    return hex(hMac.doFinal(password.toByteArray()))
}
