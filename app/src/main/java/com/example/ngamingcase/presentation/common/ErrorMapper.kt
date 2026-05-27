package com.example.ngamingcase.presentation.common

import android.content.Context
import com.example.ngamingcase.R
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.core.network.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import retrofit2.HttpException

class ErrorMapper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger
) {
    fun map(throwable: Throwable): String {
        logger.e("ErrorMapper", "Mapping error: ${throwable::class.java.simpleName}", throwable)
        return when (throwable) {
            is NoInternetException -> context.getString(R.string.error_no_internet)
            is SSLHandshakeException, is SSLPeerUnverifiedException -> context.getString(R.string.error_secure_connection)
            is HttpException -> context.getString(R.string.error_server)
            is IOException -> context.getString(R.string.error_network)
            else -> context.getString(R.string.error_unknown)
        }
    }
}
