package io.mustelidae.otter.lutrogale.web.commons.utils

import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.Throws

/**
 * Created by seooseok on 2016. 10. 17..
 */
object DecryptUtil {
    /**
     * AES 방식의 복호화
     *
     * @param value 복호화 대상 문자열
     * @return String 복호화 된 문자열
     * @throws Exception
     */
    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun aes128(key: String?, value: String?): String {
        require(!(value == null || value.isEmpty())) { "encrypt value is empty." }
        require(!(key == null || key.isEmpty())) { "encrypt key is empty." }

        // use key coss2
        val skeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        val ba = ByteArray(value.length / 2)
        for (i in ba.indices) {
            ba[i] = value.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }
        return String(cipher.doFinal(ba))
    }
}
