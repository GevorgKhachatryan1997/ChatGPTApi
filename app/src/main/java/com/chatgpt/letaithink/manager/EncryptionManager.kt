package com.chatgpt.letaithink.manager

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object EncryptionManager {

    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val ALGORITHM = "AES"
    private val KEY = "123456789012345678901234".toByteArray()

    private const val IV_SIZE = 16

    fun encrypt(input: String): String {
        val iv = generateIV()
        val ivParams = IvParameterSpec(iv)
        val cipher = createCipher(ivParams, Cipher.ENCRYPT_MODE)

        val encryptedBytes = cipher.doFinal(input.toByteArray())
        val encryptedBytesWithIV = iv + encryptedBytes
        return Base64.encodeToString(encryptedBytesWithIV, Base64.DEFAULT)
    }

    fun decrypt(input: String): String {
        val encryptedBytesWithIV = Base64.decode(input, Base64.DEFAULT)
        val iv = encryptedBytesWithIV.sliceArray(0 until IV_SIZE)
        val ivParams = IvParameterSpec(iv)
        val encryptedBytes = encryptedBytesWithIV.sliceArray(IV_SIZE until encryptedBytesWithIV.size)
        val cipher = createCipher(ivParams, Cipher.DECRYPT_MODE)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    private fun createCipher(iv: IvParameterSpec, mode: Int): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(KEY, ALGORITHM)
        cipher.init(mode, secretKeySpec, iv)
        return cipher
    }

    private fun generateIV(): ByteArray = ByteArray(IV_SIZE).apply { Random.nextBytes(this) }

}