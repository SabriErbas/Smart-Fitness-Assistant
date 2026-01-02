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
    val weeklyCompleted: Int = 0,

    // --- SON ANTRENMAN (Eski kart için) ---
    val lastWorkoutName: String = "-",
    val lastWorkoutVolume: Int = 0,
    val funResult: FunWeightResult? = null,

    // --- YENİ: GENEL TOPLAM (Yeni Fil/Tank Kartı İçin) ---
    val totalVolume: Double = 0.0,
    val animalComparison: String = "Henüz Başlamadı",

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
        // 1. Motivasyon
        _uiState.update { it.copy(dailyQuote = MotivationProvider.getRandomQuote()) }
        // 2. Plan
        observeTodayPlan()
        // 3. İstatistikler
        observeHistoryStats()
    }

    private fun observeTodayPlan() {
        viewModelScope.launch {
            workoutRepository.allWorkouts.collect { allWorkouts ->
                val today = LocalDate.now()
                val dayIndex = today.dayOfWeek.value

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
        // A. HAFTALIK İLERLEME
        viewModelScope.launch {
            val now = LocalDate.now()
            val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            // Tarih -> Milisaniye Dönüşümü
            val startMillis = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = System.currentTimeMillis() // Şu an

            workoutRepository.getWeeklyWorkoutCount(startMillis, endMillis).collect { count ->
                _uiState.update { it.copy(weeklyCompleted = count) }
            }
        }

        // B. SON ANTRENMAN (Eski FunResult yapısı korunuyor)
        viewModelScope.launch {
            workoutRepository.getLastWorkout().collect { history ->
                if (history != null) {
                    val funData = FunWeightConverter.convert(history.totalVolume)
                    _uiState.update {
                        it.copy(
                            lastWorkoutName = history.workoutName,
                            lastWorkoutVolume = history.totalVolume.toInt(),
                            funResult = funData
                        )
                    }
                } else {
                    _uiState.update { it.copy(funResult = null) }
                }
            }
        }

        // C. YENİ: TOPLAM ÖMÜR BOYU TONAJ (Fil Kartı)
        viewModelScope.launch {
            workoutRepository.getTotalVolume().collect { volume ->
                val comparisonText = calculateAnimal(volume)
                _uiState.update {
                    it.copy(
                        totalVolume = volume,
                        animalComparison = comparisonText
                    )
                }
            }
        }
    }

    // Tonajı Hayvana/Nesneye Çeviren Fonksiyon 🐘
    private fun calculateAnimal(volumeKg: Double): String {
        if (volumeKg <= 1.0) return "Henüz veri yok"

        val elephantWeight = 6000.0 // Afrika Fili
        val carWeight = 1500.0      // Araba
        val tankWeight = 60000.0    // Tank
        val humanWeight = 80.0      // İnsan

        return when {
            volumeKg < carWeight -> String.format("%.1f Yetişkin İnsan", volumeKg / humanWeight)
            volumeKg < elephantWeight -> String.format("%.1f Araba", volumeKg / carWeight)
            volumeKg < tankWeight -> String.format("%.2f Afrika Fili", volumeKg / elephantWeight)
            else -> String.format("%.2f TANK", volumeKg / tankWeight)
        }
    }
}