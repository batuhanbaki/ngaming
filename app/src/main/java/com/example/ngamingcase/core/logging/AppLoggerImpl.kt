package com.example.ngamingcase.core.logging

import android.util.Log
import com.example.ngamingcase.BuildConfig

import javax.inject.Inject

class AppLoggerImpl @Inject constructor() : AppLogger {
    override fun d(tag: String, message: String) { if (BuildConfig.DEBUG) Log.d(tag, message) }
    override fun i(tag: String, message: String) { if (BuildConfig.DEBUG) Log.i(tag, message) }
    override fun w(tag: String, message: String) { if (BuildConfig.DEBUG) Log.w(tag, message) }
    override fun e(tag: String, message: String, throwable: Throwable?) { if (BuildConfig.DEBUG) Log.e(tag, message, throwable) }
}
