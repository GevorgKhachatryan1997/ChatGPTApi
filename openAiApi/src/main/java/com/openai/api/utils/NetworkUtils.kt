package com.openai.api.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.openai.api.exception.NoConnectionException

@SuppressLint("StaticFieldLeak")
object NetworkUtils {

    private lateinit var context: Context

    fun init(context: Context) {
        NetworkUtils.context = context
    }

    @Throws(NoConnectionException::class)
    fun ensureNetworkConnection() {
        if (!isInternetConnected()) {
            throw NoConnectionException()
        }
    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

}