package io.mustelidae.otter.lutrogale.web.commons.utils

import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by seooseok on 2016. 9. 26..
 */
object EncryptUtil {
    @JvmStatic
    fun sha256(value: String?): String {
        if (value == null || value.isEmpty()) return ""
        val encrypt: String = try {
            val sh = MessageDigest.getInstance("SHA-256")
            sh.update(value.toByteArray())
            val byteData = sh.digest()
            val sb = StringBuilder()
            for (data in byteData) {
                sb.append(((data.toInt() and 0xff) + 0x100).toString(16).substring(1))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
        return encrypt
    }
}
