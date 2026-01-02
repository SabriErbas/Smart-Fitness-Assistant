package com.example.gymapp002.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import com.example.gymapp002.data.local.entity.WorkoutHistoryLog
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExerciseSetItem(
    val exerciseId: Int,
    val exerciseName: String, // İsim bilgisini de tutalım ki loglarken kolay olsun
    val setNumber: Int,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean = false
)

data class ActiveWorkoutUiState(
    val workoutTitle: String = "",
    val setList: Map<Int, List<ExerciseSetItem>> = emptyMap(),
    val exerciseOrder: List<WorkoutDetailItem> = emptyList(),
    val timerSeconds: Long = 0,
    val isPaused: Boolean = false,
    val isLoading: Boolean = true
)

class ActiveWorkoutViewModel(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadWorkoutData()
        startTimer()
    }

    private fun loadWorkoutData() {
        viewModelScope.launch {
            val info = workoutRepository.getWorkoutById(workoutId).first()
            val details = workoutRepository.getWorkoutDetails(workoutId).first()

            val expandedSets = mutableMapOf<Int, List<ExerciseSetItem>>()

            details.forEach { detail ->
                val setsForExercise = List(detail.sets) { index ->
                    ExerciseSetItem(
                        exerciseId = detail.exercise.exerciseId,
                        exerciseName = detail.exercise.name, // İsmi buraya ekledik
                        setNumber = index + 1,
                        weight = "",
                        reps = detail.reps,
                        isCompleted = false
                    )
                }
                expandedSets[detail.exercise.exerciseId] = setsForExercise
            }

            _uiState.update {
                it.copy(
                    workoutTitle = info?.workout?.workoutName ?: "Antrenman",
                    setList = expandedSets,
                    exerciseOrder = details,
                    isLoading = false
                )
            }
        }
    }

    // --- KULLANICI GİRDİLERİ ---

    fun updateSetData(exerciseId: Int, setIndex: Int, newWeight: String, newReps: String) {
        val currentMap = _uiState.value.setList.toMutableMap()
        val currentList = currentMap[exerciseId]?.toMutableList() ?: return

        val updatedItem = currentList[setIndex].copy(weight = newWeight, reps = newReps)
        currentList[setIndex] = updatedItem

        currentMap[exerciseId] = currentList
        _uiState.update { it.copy(setList = currentMap) }
    }

    fun toggleSetComplete(exerciseId: Int, setIndex: Int) {
        val currentMap = _uiState.value.setList.toMutableMap()
        val currentList = currentMap[exerciseId]?.toMutableList() ?: return

        val item = currentList[setIndex]
        val updatedItem = item.copy(isCompleted = !item.isCompleted)
        currentList[setIndex] = updatedItem

        currentMap[exerciseId] = currentList
        _uiState.update { it.copy(setList = currentMap) }
    }

    // --- KAYIT VE BİTİRME (YENİ EKLENEN KISIM) ---
    fun finishWorkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            // 1. Toplam Tonaj Hesabı (Sadece tamamlanan setler)
            var totalVolume = 0.0
            val allLogs = mutableListOf<WorkoutHistoryLog>()

            state.setList.values.flatten().forEach { setItem ->
                if (setItem.isCompleted) {
                    val w = setItem.weight.toDoubleOrNull() ?: 0.0
                    val r = setItem.reps.toIntOrNull() ?: 0
                    totalVolume += (w * r)
                }

                // Log Listesini Hazırla
                allLogs.add(
                    WorkoutHistoryLog(
                        historyId = 0, // Repository'de atanacak
                        exerciseName = setItem.exerciseName,
                        setNumber = setItem.setNumber,
                        weight = setItem.weight.toDoubleOrNull() ?: 0.0,
                        reps = setItem.reps.toIntOrNull() ?: 0,
                        isCompleted = setItem.isCompleted
                    )
                )
            }

            // 2. Süre Formatı
            val minutes = state.timerSeconds / 60
            val seconds = state.timerSeconds % 60
            val durationStr = String.format("%02d:%02d", minutes, seconds)

            // 3. Geçmiş Nesnesini Oluştur
            val history = WorkoutHistoryEntity(
                workoutName = state.workoutTitle,
                date = LocalDate.now(),
                duration = durationStr,
                totalVolume = totalVolume,
                caloriesBurnt = (state.timerSeconds / 60 * 5).toInt() // Dakikada 5 kalori (tahmini)
            )

            // 4. Repository'ye Gönder
            workoutRepository.saveWorkoutSession(history, allLogs)

            // 5. İşlem bitti, ekranı kapatabiliriz
            onSuccess()
        }
    }

    // --- KRONOMETRE ---
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (!_uiState.value.isPaused) {
                    _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                }
            }
        }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}