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
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // 1. Kullanıcının takvimde seçtiği tarih
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // 2. Veritabanındaki TÜM antrenmanlar (Otomatik güncellenir)
    private val allWorkouts = workoutRepository.allWorkouts

    // 3. GÜNLÜK PLAN (Filtrelenmiş Liste)
    // Tarih değiştikçe veya yeni antrenman eklendikçe burası otomatik hesaplanır.
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

    // --- FİLTRELEME MANTIĞI ---
    private fun filterWorkoutsForDate(date: LocalDate, workouts: List<WorkoutWithExercises>): List<WorkoutWithExercises> {
        // Seçilen günün indexi (Pzt=1, Sal=2 ... Paz=7)
        val dayIndex = date.dayOfWeek.value

        return workouts.filter { item ->
            val w = item.workout

            if (w.scheduleType == "WEEKLY") {
                // Eğer "1,3,5" string'i içinde bugünün indexi ("1") varsa, listeye ekle.
                val days = w.recurrenceDays.split(",")
                days.contains(dayIndex.toString())
            } else {
                // Döngüsel mantık (V2'de detaylandırılabilir, şimdilik pas geçiyoruz veya her gün gösteriyoruz)
                // Basitlik için şimdilik döngüsel antrenmanları her gün gösterelim mi?
                // Ya da sadece WEEKLY çalışsın şimdilik.
                false
            }
        }
    }
}