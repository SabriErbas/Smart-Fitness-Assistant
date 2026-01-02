package com.example.gymapp002.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // 1. Kullanıcının takvimde seçtiği tarih
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // 2. Veritabanındaki TÜM antrenmanlar (Ham veri)
    private val allWorkouts = workoutRepository.allWorkouts

    // --- YENİ: KÜTÜPHANEYİ İKİYE BÖLÜYORUZ ---

    // A. Kullanıcının Kendi Oluşturdukları (isSystemWorkout = false)
    val userLibraryWorkouts: StateFlow<List<WorkoutWithExercises>> = allWorkouts
        .map { list -> list.filter { !it.workout.isSystemWorkout } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // B. Sistemin Hazır Programları (isSystemWorkout = true)
    val systemLibraryWorkouts: StateFlow<List<WorkoutWithExercises>> = allWorkouts
        .map { list -> list.filter { it.workout.isSystemWorkout } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // -----------------------------------------------

    // 3. GÜNLÜK PLAN (Takvim Mantığı - AYNI KALDI)
    val dailyPlan: StateFlow<List<WorkoutWithExercises>> = combine(_selectedDate, allWorkouts) { date, workouts ->
        filterWorkoutsForDate(date, workouts)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Tarihi değiştirme fonksiyonu
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    // --- FİLTRELEME MANTIĞI (AYNI KALDI) ---
    private fun filterWorkoutsForDate(date: LocalDate, workouts: List<WorkoutWithExercises>): List<WorkoutWithExercises> {
        val dayIndex = date.dayOfWeek.value
        return workouts.filter { item ->
            val w = item.workout
            if (w.scheduleType == "WEEKLY") {
                val days = w.recurrenceDays.split(",")
                days.contains(dayIndex.toString())
            } else {
                false
            }
        }
    }

    // Silme fonksiyonu
    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workoutId)
        }
    }
}