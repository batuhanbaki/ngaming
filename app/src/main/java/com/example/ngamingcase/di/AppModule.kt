package com.example.ngamingcase.di

import com.example.ngamingcase.BuildConfig
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.core.logging.AppLoggerImpl
import com.example.ngamingcase.core.network.NetworkConnectionInterceptor
import com.example.ngamingcase.data.remote.api.PostsApiService
import com.example.ngamingcase.data.repository.PostRepositoryImpl
import com.example.ngamingcase.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApi(
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): PostsApiService {
        val httpLogger = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val pinner = CertificatePinner.Builder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/e89QAFJvkB7Tn3QGfsNheN8fgTxZgLECjap1xSq628w=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4="
            )
            .build()
        val client = OkHttpClient.Builder()
            .certificatePinner(pinner)
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(httpLogger)
            .build()

        return Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PostsApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepo(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindLogger(impl: AppLoggerImpl): AppLogger
}
