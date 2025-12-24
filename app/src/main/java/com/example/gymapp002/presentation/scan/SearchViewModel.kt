package com.example.gymapp002.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.gymapp002.data.local.entity.Exercise

class SearchViewModel(private val repository: ExerciseRepository) : ViewModel() {

    // UI'ın dinleyeceği egzersiz listesi
    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
    val exerciseList: StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    // Kullanıcının yazdığı arama metni
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // ViewModel ilk oluştuğunda tüm listeyi getir
        getAllExercises()
    }

    // Kullanıcı harf yazdıkça burası çalışacak
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            getAllExercises()
        } else {
            searchDatabase(query)
        }
    }

    private fun getAllExercises() {
        viewModelScope.launch {
            // Repository'den gelen akışı (Flow) dinle ve listeyi güncelle
            repository.allExercises.collectLatest { exercises ->
                _exerciseList.value = exercises
            }
        }
    }

    private fun searchDatabase(query: String) {
        viewModelScope.launch {
            repository.searchExercises(query).collectLatest { exercises ->
                _exerciseList.value = exercises
            }
        }
    }
}