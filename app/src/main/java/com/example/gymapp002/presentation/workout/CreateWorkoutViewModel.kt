package com.example.gymapp002.ui.screens

import androidx.compose.animation.Crossfade
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.ExerciseEntity
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.repository.ExerciseRepository
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.roundToInt

data class CreateWorkoutUiState(
    val workoutName: String = "",
    val difficulty: String = "Intermediate",
    val calculatedDurationMin: Int = 0,

    // MOLA VE PLANLAMA
    val restTimeSeconds: Int = 60,
    val scheduleType: String = "WEEKLY",
    val selectedDays: Set<Int> = emptySet(),
    val recurrenceGap: Int = 2,

    val selectedExercises: List<SelectedExerciseItem> = emptyList(),
    val isSaveEnabled: Boolean = false,

    // YENİ: SEÇİCİ (PICKER) İÇİN STATE
    val isPickerVisible: Boolean = false,     // Pencere açık mı?
    val availableExercises: List<ExerciseEntity> = emptyList(), // Tüm egzersizler
    val filteredExercises: List<ExerciseEntity> = emptyList(),  // Arama sonucu
    val searchQuery: String = ""             // Arama metni
)

data class SelectedExerciseItem(
    val exercise: ExerciseEntity,
    val sets: String = "3",
    val reps: String = "10"
)

class CreateWorkoutViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWorkoutUiState())
    val uiState: StateFlow<CreateWorkoutUiState> = _uiState.asStateFlow()

    init {
        // ViewModel başladığında veritabanındaki tüm hareketleri yükle
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            try {
                // Veriyi güvenli bir şekilde çekmeye çalış
                exerciseRepository.allExercises.collect { allList ->
                    _uiState.update {
                        it.copy(
                            availableExercises = allList,
                            filteredExercises = allList // Başlangıçta hepsi görünür
                        )
                    }
                }
            } catch (e: Exception) {
                // Hata olursa logla ama uygulamayı çökertme
                e.printStackTrace()
            }
        }
    }

    // --- SEÇİCİ PENCERESİ YÖNETİMİ ---

    fun togglePicker(show: Boolean) {
        _uiState.update { it.copy(isPickerVisible = show, searchQuery = "") }
        // Pencere her açıldığında filtreyi sıfırla
        if (show) filterExercises("")
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterExercises(query)
    }

    private fun filterExercises(query: String) {
        val all = _uiState.value.availableExercises
        val filtered = if (query.isBlank()) {
            all
        } else {
            all.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.muscleGroup.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredExercises = filtered) }
    }

    // --- MEVCUT FONKSİYONLAR (Aynı) ---

    fun addExercise(exercise: ExerciseEntity) {
        val newItem = SelectedExerciseItem(exercise)
        val currentList = _uiState.value.selectedExercises + newItem
        updateStateWithCalculation(selectedExercises = currentList)
        // Eklendikten sonra pencereyi kapat
        togglePicker(false)
    }

    fun removeExercise(item: SelectedExerciseItem) {
        val currentList = _uiState.value.selectedExercises - item
        updateStateWithCalculation(selectedExercises = currentList)
    }

    fun moveExercise(index: Int, direction: Int) {
        val currentList = _uiState.value.selectedExercises.toMutableList()
        val newIndex = index + direction
        if (newIndex in 0 until currentList.size) {
            Collections.swap(currentList, index, newIndex)
            _uiState.update { it.copy(selectedExercises = currentList) }
        }
    }

    fun updateName(name: String) {
        updateStateWithCalculation(workoutName = name)
    }

    fun updateRestTime(seconds: Int) {
        updateStateWithCalculation(restTimeSeconds = seconds)
    }

    fun toggleDaySelection(dayIndex: Int) {
        val currentDays = _uiState.value.selectedDays.toMutableSet()
        if (currentDays.contains(dayIndex)) currentDays.remove(dayIndex) else currentDays.add(dayIndex)
        updateStateWithCalculation(selectedDays = currentDays)
    }

    fun setScheduleType(type: String) {
        updateStateWithCalculation(scheduleType = type)
    }

    // Hesaplama ve Kaydetme mantığı aynı...
    private fun calculateDuration(exercises: List<SelectedExerciseItem>, restTimeSec: Int): Int {
        if (exercises.isEmpty()) return 0
        var totalSets = 0
        exercises.forEach { totalSets += it.sets.toIntOrNull() ?: 3 }
        val workTimeSec = totalSets * 45
        val totalRestTimeSec = if (totalSets > 0) (totalSets - 1) * restTimeSec else 0
        return ((workTimeSec + totalRestTimeSec) / 60.0).roundToInt()
    }

    private fun updateStateWithCalculation(
        workoutName: String = _uiState.value.workoutName,
        selectedExercises: List<SelectedExerciseItem> = _uiState.value.selectedExercises,
        restTimeSeconds: Int = _uiState.value.restTimeSeconds,
        scheduleType: String = _uiState.value.scheduleType,
        selectedDays: Set<Int> = _uiState.value.selectedDays,
        recurrenceGap: Int = _uiState.value.recurrenceGap
    ) {
        val duration = calculateDuration(selectedExercises, restTimeSeconds)
        val isScheduleValid = if (scheduleType == "WEEKLY") selectedDays.isNotEmpty() else true
        val isValid = workoutName.isNotBlank() && selectedExercises.isNotEmpty() && isScheduleValid

        _uiState.update {
            it.copy(
                workoutName = workoutName,
                selectedExercises = selectedExercises,
                restTimeSeconds = restTimeSeconds,
                calculatedDurationMin = duration,
                scheduleType = scheduleType,
                selectedDays = selectedDays,
                recurrenceGap = recurrenceGap,
                isSaveEnabled = isValid
            )
        }
    }

    fun saveWorkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val daysString = state.selectedDays.sorted().joinToString(",")

            val newWorkout = WorkoutEntity(
                workoutName = state.workoutName,
                difficulty = state.difficulty,
                duration = "${state.calculatedDurationMin} dk",
                scheduleType = state.scheduleType,
                recurrenceDays = daysString,
                recurrenceGap = state.recurrenceGap,
                description = "Mola: ${state.restTimeSeconds}sn"
            )

            val crossRefs = state.selectedExercises.mapIndexed { index, item ->
                WorkoutExerciseCrossRef(
                    workoutId = 0,
                    exerciseId = item.exercise.exerciseId,
                    sets = item.sets.toIntOrNull() ?: 3,
                    reps = item.reps,
                    order = index + 1
                )
            }
            workoutRepository.createWorkout(newWorkout, crossRefs = crossRefs)
            onSuccess()
        }
    }

    // --- EKSİK OLAN FONKSİYON: SET/TEKRAR GÜNCELLEME ---
    fun updateExerciseDetails(index: Int, sets: String, reps: String) {
        val currentList = _uiState.value.selectedExercises.toMutableList()
        if (index in currentList.indices) {
            // İlgili elemanı kopyala ve yeni değerleri ver
            val updatedItem = currentList[index].copy(sets = sets, reps = reps)
            currentList[index] = updatedItem

            // Listeyi güncelle ve SÜREYİ TEKRAR HESAPLA
            updateStateWithCalculation(selectedExercises = currentList)
        }
    }
}