package com.example.ngamingcase.core.network

import com.example.ngamingcase.core.logging.AppLogger
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val logger: AppLogger
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpoint = "${request.url.host}${request.url.encodedPath}"
        if (!networkMonitor.isConnected()) {
            logger.w("NetworkInterceptor", "request blocked: no internet ($endpoint)")
            throw NoInternetException()
        }
        logger.d("NetworkInterceptor", "request allowed ($endpoint)")
        return chain.proceed(request)
    }
}
