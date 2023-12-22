package com.dicoding.fruition1.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference

class RegisterViewModelFactory(
    private val repository: UserRepository,
    private val context: Context,
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository, context, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
