package com.example.gymapp002.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WorkoutDetailUiState(
    val workoutInfo: WorkoutWithExercises? = null, // Başlık, süre vb. için
    val exercises: List<WorkoutDetailItem> = emptyList(), // Sıralı liste
    val isLoading: Boolean = true
)

class WorkoutDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    // 1. Antrenman Başlık Bilgisi
    private val workoutInfoFlow = workoutRepository.getWorkoutById(workoutId)

    // 2. Sıralı Egzersiz Listesi (Yeni yazdığımız Query)
    private val exercisesFlow = workoutRepository.getWorkoutDetails(workoutId)

    // İkisini birleştirip UI State oluşturuyoruz
    val uiState: StateFlow<WorkoutDetailUiState> = combine(workoutInfoFlow, exercisesFlow) { info, list ->
        WorkoutDetailUiState(
            workoutInfo = info,
            exercises = list,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutDetailUiState(isLoading = true)
    )

    // --- SIRALAMA DEĞİŞTİRME (YUKARI / AŞAĞI) ---
    fun moveExercise(index: Int, direction: Int) {
        val currentList = uiState.value.exercises
        val newIndex = index + direction

        // Liste sınırları içinde miyiz?
        if (newIndex in currentList.indices) {
            val item1 = currentList[index]
            val item2 = currentList[newIndex]

            viewModelScope.launch {
                // Repository üzerinden veritabanında yer değiştir
                workoutRepository.swapExercisesOrder(workoutId, item1, item2)
            }
        }
    }

}