package io.mustelidae.otter.lutrogale.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Crypto(
    key: String
) {

    private val iv = IvParameterSpec("cCaJdltxT15KxPw".toByteArray())
    private val algorithm = "AES/CBC/PKCS5Padding"
    private val keySpec = SecretKeySpec(key.toByteArray(), "AES")
    private val cipher = Cipher.getInstance(algorithm)
    private val jackson = Jackson.getMapper()

    fun enc(value: Any): String {
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val json = jackson.writeValueAsString(value)
        val byteArrayOfEncrypt = cipher.doFinal(json.toByteArray(Charsets.UTF_8))

        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteArrayOfEncrypt)
    }

    fun <T> dec(text: String, valueType: Class<T>): T {
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val cipherText = Base64.getUrlDecoder().decode(text)
        val decryptedText = String(cipher.doFinal(cipherText), Charsets.UTF_8)

        return jackson.readValue(decryptedText, valueType)
    }
}
