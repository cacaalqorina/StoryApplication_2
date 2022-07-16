package com.annisaalqorina.submissionstory.di

import androidx.viewbinding.BuildConfig
import com.annisaalqorina.submissionstory.api.MyApi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object Module {

    var BASE_URL_API = "https://story-api.dicoding.dev/v1/"

    @Provides
    fun provideRetrofit(): Retrofit {
        val interceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): MyApi {
        return retrofit.create(MyApi::class.java)
    }
}