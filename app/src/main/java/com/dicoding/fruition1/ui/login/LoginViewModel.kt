package com.dicoding.fruition1.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.fruition1.api.LoginRequestBody
import com.dicoding.fruition1.api.LoginResponse
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(application: Context, private val repository: UserRepository) : ViewModel() {
    val context = application
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: MutableLiveData<LoginResponse?> = _loginResponse

    fun login(loginRequest: LoginRequestBody) {
        viewModelScope.launch {
            _isLoading.value = true
            // Call the login API
            val response = repository.loginUser(loginRequest)

            _loginResponse.value = response



            fun navigateToHome() {
                _navigateToHome.value = true
            }

            if (response.success == 1) {
                _isLoading.value = false
                // Login successful, save the user's session and token to DataStore
                val userModel = UserModel(
                    email = response.email ?: "",
                    username = response.username?:"",
                   token = response.token,
                    true
                )
                repository.saveSession(userModel)

                navigateToHome()

            } else {
                _isLoading.value = false
                Toast.makeText(
                    context,
                    "Incorrect login information. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            _loginResponse.value = null
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }


}



