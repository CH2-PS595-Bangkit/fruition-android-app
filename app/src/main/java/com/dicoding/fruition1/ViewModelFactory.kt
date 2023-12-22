package com.dicoding.fruition1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.fruition1.data.HistoryRepository
import com.dicoding.fruition1.ui.home.HomeViewModel

class ViewModelFactory(private val repository: HistoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
            throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }



