package com.gc.myapplication.di
import okhttp3.Interceptor
import org.koin.dsl.module

val networkModule = module {
    single {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }
    }
}