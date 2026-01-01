package com.example.gymapp002.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.repository.WorkoutRepository
import com.example.gymapp002.util.FunWeightConverter
import com.example.gymapp002.util.FunWeightResult
import com.example.gymapp002.util.MotivationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class HomeUiState(
    val userName: String = "Sabri",
    val streakDays: Int = 0,
    val weeklyGoal: Int = 4,
    val weeklyCompleted: Int = 0,       // ARTIK GERÇEK VERİ
    val lastWorkoutName: String = "-",
    val lastWorkoutVolume: Int = 0,
    val funResult: FunWeightResult? = null, // ARTIK GERÇEK VERİ
    val dailyQuote: String = "",
    val todaysWorkout: WorkoutEntity? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // 1. Motivasyon Sözü
        _uiState.update { it.copy(dailyQuote = MotivationProvider.getRandomQuote()) }

        // 2. Bugünün Planı
        observeTodayPlan()

        // 3. Geçmiş İstatistikleri (YENİ)
        observeHistoryStats()
    }

    private fun observeTodayPlan() {
        viewModelScope.launch {
            workoutRepository.allWorkouts.collect { allWorkouts ->
                val today = LocalDate.now()
                val dayIndex = today.dayOfWeek.value // 1=Pzt ... 7=Pazar

                val foundWorkout = allWorkouts.firstOrNull { item ->
                    val w = item.workout
                    if (w.scheduleType == "WEEKLY") {
                        val days = w.recurrenceDays.split(",")
                        days.contains(dayIndex.toString())
                    } else {
                        false
                    }
                }
                _uiState.update { it.copy(todaysWorkout = foundWorkout?.workout) }
            }
        }
    }

    private fun observeHistoryStats() {
        viewModelScope.launch {
            // A. HAFTALIK İLERLEME (Bu Pazartesi - Şu An)
            val now = LocalDate.now()
            // Bu haftanın Pazartesi gününü bul
            val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            // Milisaniyeye çevir (Veritabanı Long tutuyor)
            val startMillis = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = System.currentTimeMillis()

            // Veritabanını dinle
            workoutRepository.getWeeklyWorkoutCount(startMillis, endMillis).collect { count ->
                _uiState.update { it.copy(weeklyCompleted = count) }
            }
        }

        viewModelScope.launch {
            // B. SON ANTRENMAN (Fil/Tank Kartı)
            workoutRepository.getLastWorkout().collect { history ->
                if (history != null) {
                    val funData = FunWeightConverter.convert(history.totalVolume)
                    _uiState.update {
                        it.copy(
                            lastWorkoutName = history.workoutName,
                            lastWorkoutVolume = history.totalVolume,
                            funResult = funData
                        )
                    }
                } else {
                    // Hiç geçmiş yoksa boş göster
                    _uiState.update { it.copy(funResult = null) }
                }
            }
        }
    }
}