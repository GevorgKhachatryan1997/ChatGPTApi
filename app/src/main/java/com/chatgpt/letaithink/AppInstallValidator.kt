package com.chatgpt.letaithink

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest
import java.util.*

object AppInstallValidator {
    private const val PLAY_STORE_APP_ID = "com.android.vending"
    private const val ORIGINAL_SIGNATURE = "7x7+6o2rr6sf1q0er/7y/YMDHtwbFvjO+VzVxU8RrCnC5VCwjIDSXK0cvpJr6oDh5EuibGTyKMBvLZCgWAdhlg=="

    fun validateAppInstallation(context: Context) {
        if (BuildConfig.TAMPER_DETECTION_ENABLED) {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                context.packageManager.getInstallerPackageName(context.packageName)
            }

            // Validate the installer
            if (installer?.startsWith(PLAY_STORE_APP_ID) == true) {
                // Validate app signature
                context.getAppSignature()?.encrypt()?.let {
                    if (ORIGINAL_SIGNATURE == it) return@validateAppInstallation
                }
            }

            throw RuntimeException("Unable to run the application")
        }
    }

    private fun Context.getAppSignature(): Signature? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures.firstOrNull()
        } else {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo.apkContentsSigners.firstOrNull()
        }

    private fun Signature.encrypt(): String? = try {
        with(MessageDigest.getInstance("SHA-512").digest(toByteArray())) {
            Base64.getEncoder().encodeToString(this)
        }
    } catch (exception: Exception) {
        null
    }
}
