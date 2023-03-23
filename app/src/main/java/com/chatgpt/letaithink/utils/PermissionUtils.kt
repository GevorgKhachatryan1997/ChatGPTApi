package com.chatgpt.letaithink.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean =
        checkPermissionGranted(ContextCompat.checkSelfPermission(context, permission))

    fun checkPermissionGranted(grantedResult: Int) = grantedResult == PackageManager.PERMISSION_GRANTED

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!hasPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    private fun shouldDisplayRationale(activity: Activity, permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
}
