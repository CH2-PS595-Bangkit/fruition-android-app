package com.dicoding.fruition1.ui.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.api.ErrorDetail
import com.dicoding.fruition1.api.ErrorResponse
import com.dicoding.fruition1.api.Injection
import com.dicoding.fruition1.api.RegisterRequest
import com.dicoding.fruition1.api.SuccessResponse
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository,
                        private val context: Context,
                        private val userPreference: UserPreference
) : ViewModel() {


    private lateinit var apiService: ApiService


    fun fetchDataFromBackend() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiService = Injection.provideApiService(userPreference)
                val message = Injection.fetchMessageFromBackend(apiService)
                Log.d("RegisterViewModel", "Message from backend: $message")
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error fetching data from backend: ${e.message}")
            }
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    suspend fun registerUser(
        email: String,
        username: String,
        password: String
    ): UserRepository.RegistrationResult {
        try {
            // Input validation
            if (email.isBlank() || username.isBlank() || password.isBlank()) {
                return UserRepository.RegistrationResult.Error(
                    ErrorResponse(
                        errors = listOf(
                            ErrorDetail(
                                type = "registration",
                                msg = "",
                                path = "",
                                location = "body"
                            )
                        )
                    )
                )
            }

            apiService = Injection.provideApiService(userPreference)
            val request = RegisterRequest(username, email, password)
            val response = apiService.registerUser(request)

            // Check for success or failure in the API response
            if (response.success == 1) {
                // Extract relevant information from RegisterResponse
                val successResponse = SuccessResponse(
                    success = 1,
                    message = response.message ?: "",
                    userId = response.userId ?: 0
                )
                Log.d("Register", "Pendaftaran berhasil: ${response.message}")
                return UserRepository.RegistrationResult.Success(successResponse)
            } else {
                Log.e("Register", "Pendaftaran gagal: ${response.message}")
                return UserRepository.RegistrationResult.Error(
                    ErrorResponse(
                        errors = listOf(
                            ErrorDetail(
                                type = "registration",
                                msg = response.message ?: "",
                                path = "",
                                location = "body"
                            )
                        )
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("Register", " ${e.message}")
            showToast("Invalid email or already registered. Please try again.")
            return UserRepository.RegistrationResult.Error(
                ErrorResponse(
                    errors = listOf(
                        ErrorDetail(
                            type = "registration",
                            msg = "An error occurred during registration",
                            path = "",
                            location = "body"
                        )
                    )
                )
            )
        }
    }

    fun showToast(message: String) {
        // Display a toast with the provided message
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
