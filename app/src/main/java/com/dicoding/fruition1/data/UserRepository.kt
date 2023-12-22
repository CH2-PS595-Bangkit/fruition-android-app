package com.dicoding.fruition1.data

import android.util.Log
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.api.ErrorDetail
import com.dicoding.fruition1.api.ErrorResponse
import com.dicoding.fruition1.api.LoginRequestBody
import com.dicoding.fruition1.api.LoginResponse
import com.dicoding.fruition1.api.SuccessResponse
import com.dicoding.fruition1.data.pref.UserModel
import com.dicoding.fruition1.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun loginUser(loginRequest: LoginRequestBody): LoginResponse {
        return apiService.loginUser(loginRequest)
    }


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    sealed class RegistrationResult {
        data class Success(val data: SuccessResponse) : RegistrationResult()
        data class Error(val errorData: ErrorResponse) : RegistrationResult()
    }

    sealed class LoginResult {
        data class Success(val data: SuccessResponse) : LoginResult()
        data class Error(val errorData: ErrorResponse) : LoginResult()
    }

    suspend fun registerUser(email: String, username: String, password: String): RegistrationResult {
        val response = apiService.register(email, username, password)

        return if (response.success == 1) {
            // Extract relevant information from RegisterResponse
            val successResponse = SuccessResponse(
                success = 1,  // You may need to modify this based on your API response
                message = response.message ?: "",
                userId = response.userId ?: 0
            )
            Log.d("Register", "Pendaftaran berhasil: ${response.message}")
            RegistrationResult.Success(successResponse)
        } else {
            Log.e("Register", "Pendaftaran gagal: ${response.message}")
            RegistrationResult.Error(ErrorResponse(errors = listOf(
                ErrorDetail(type = "registration", msg = response.message ?: "", path = "", location = "body")
            )))
        }
    }


    suspend fun loginUser(email: String, password: String): LoginResponse {
        // Call the login API endpoint using apiService.login
        val response = apiService.login(email, password)

        if (response.success == 0) {
            Log.e("Login", "Login failed: ${response.message}")
        } else {
            Log.d("Login", "Login successful")
        }

        return response
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
