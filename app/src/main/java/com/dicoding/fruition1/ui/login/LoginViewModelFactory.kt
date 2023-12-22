package com.dicoding.fruition1.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.fruition1.data.UserRepository

class LoginViewModelFactory(
    private val applicationContext: Context,
    private val repository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(applicationContext ,repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
