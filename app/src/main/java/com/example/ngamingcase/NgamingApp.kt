package com.example.ngamingcase

import android.app.Application
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.core.network.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NgamingApp : Application() {
    @Inject lateinit var logger: AppLogger
    @Inject lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()
        logger.i("NgamingApp", "App started")
        networkMonitor.start()
        logger.i("NgamingApp", "Network monitor initialized")
    }

    override fun onTerminate() {
        networkMonitor.stop()
        super.onTerminate()
    }
}
