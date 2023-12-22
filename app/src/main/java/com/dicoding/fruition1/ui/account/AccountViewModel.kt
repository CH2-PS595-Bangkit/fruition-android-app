package com.dicoding.fruition1.ui.account

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import kotlinx.coroutines.launch


class AccountViewModel( val repository: UserRepository) : ViewModel() {
    lateinit var userPreferencesDataStore: DataStore<Preferences>

    constructor(context: Context) : this(
        UserRepository.getInstance(
            UserPreference.getInstance(context.dataStore),
            ApiService.create("http://10.0.2.2:3000/")
        )
    ) {
        userPreferencesDataStore = context.dataStore
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}


