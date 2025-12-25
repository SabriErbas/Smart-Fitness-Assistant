package com.example.gymapp002.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(
    savedStateHandle: SavedStateHandle, // Navigasyondan gelen veriyi tutan paket
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // Adresteki "workoutId" parametresini yakalıyoruz
    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    // Ekranda göstereceğimiz veri
    private val _workoutDetails = MutableStateFlow<WorkoutWithExercises?>(null)
    val workoutDetails: StateFlow<WorkoutWithExercises?> = _workoutDetails.asStateFlow()

    init {
        // ViewModel oluşur oluşmaz veriyi çek
        getWorkoutDetails()
    }

    private fun getWorkoutDetails() {
        viewModelScope.launch {
            // Repository'de tek bir antrenman getiren fonksiyonu çağırıyoruz
            // Not: Eğer Repository'de bu fonksiyon yoksa hata verebilir, 
            // ama Dao'da getWorkoutWithExercisesById yazmıştık, Repository'e ekleyeceğiz.
            workoutRepository.getWorkoutById(workoutId).collect {
                _workoutDetails.value = it
            }
        }
    }
}