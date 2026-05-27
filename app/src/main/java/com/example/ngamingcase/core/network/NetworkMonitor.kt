package com.example.ngamingcase.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.ngamingcase.core.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger
) {
    private val tag = "NetworkMonitor"
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @Volatile private var networkAvailable: Boolean = false

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { networkAvailable = true; logger.i(tag, "network available") }
        override fun onLost(network: Network) { networkAvailable = false; logger.w(tag, "network lost") }
        override fun onLosing(network: Network, maxMsToLive: Int) { logger.w(tag, "network losing") }
        override fun onUnavailable() { networkAvailable = false; logger.w(tag, "network unavailable") }
    }

    fun start() {
        val active = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(active)
        networkAvailable = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        logger.i(tag, "network callback registered")
    }

    fun stop() {
        runCatching { connectivityManager.unregisterNetworkCallback(callback) }
            .onFailure { logger.e(tag, "network callback unregister error", it) }
    }

    fun isConnected(): Boolean = networkAvailable
}
