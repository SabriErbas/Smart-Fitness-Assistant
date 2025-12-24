package com.example.gymapp002.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gymapp002.GymApplication
import com.example.gymapp002.data.repository.ExerciseRepository
import com.example.gymapp002.ui.screens.SearchViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // SearchViewModel Nasıl Üretilir? Tarifi burada:
        initializer {
            // 1. Önce Application sınıfına eriş
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            // 2. Repository'yi oluştur
            val repository = ExerciseRepository(application.database.exerciseDao())
            // 3. ViewModel'i repository ile başlat ve döndür
            SearchViewModel(repository)
        }
    }
}