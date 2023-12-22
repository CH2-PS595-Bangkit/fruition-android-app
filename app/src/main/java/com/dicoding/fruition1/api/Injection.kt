package com.dicoding.fruition1.api

import android.content.Context
import android.util.Log
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = provideApiService( pref)
        return UserRepository.getInstance(pref, apiService)
    }

    /*fun provideStoryRepository(context: Context): HistoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return HistoryRepository.getInstance(provideApiService(context, pref), pref)
    }*/

    suspend fun fetchMessageFromBackend(apiService: ApiService): String {
        return try {
            Log.d("Injection", "Fetching data from the backend...")
            val response = apiService.getServiceMessage()
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Empty response"
                Log.d("Injection", "Data fetched successfully: $message")
                message
            } else {
                val error = "Error: ${response.code()}"
                Log.e("Injection", error)
                error
            }
        } catch (e: Exception) {
            val error = "Error: ${e.message}"
            Log.e("Injection", error)
            error
        }
    }

    fun provideApiService(pref: UserPreference): ApiService {
        val user = runBlocking { pref.getUser().first() }
        val token = user?.token
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun provideApiServiceUp(pref: UserPreference): ApiService {
        val user = runBlocking { pref.getUser().first() }
        val token = user?.token
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}



data class MessageResponse(val message: String)







