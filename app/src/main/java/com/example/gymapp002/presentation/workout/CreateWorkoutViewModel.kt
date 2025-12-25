package com.example.gymapp002.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp002.data.local.entity.Exercise
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.repository.ExerciseRepository
import com.example.gymapp002.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SelectedExerciseState(
    val exercise: Exercise,
    var sets: String = "3",
    var reps: String = "10"
)

// YENİ: Planlama Tipi
enum class ScheduleType {
    WEEKLY, // Pzt, Çar, Cum gibi
    CYCLIC  // Her 3 günde bir gibi
}

class CreateWorkoutViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _allExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val allExercises: StateFlow<List<Exercise>> = _allExercises.asStateFlow()

    val selectedExercises = mutableStateListOf<SelectedExerciseState>()

    private val _workoutName = MutableStateFlow("")
    val workoutName: StateFlow<String> = _workoutName.asStateFlow()

    // --- YENİ EKLENEN PLANLAMA STATE'LERİ ---
    private val _scheduleType = MutableStateFlow(ScheduleType.WEEKLY)
    val scheduleType: StateFlow<ScheduleType> = _scheduleType.asStateFlow()

    // Haftalık Mod için seçilen günler (1=Pzt, 7=Paz)
    val selectedDays = mutableStateListOf<Int>()

    // Döngüsel Mod için gün aralığı (Örn: 3)
    private val _cycleGap = MutableStateFlow("2") // Varsayılan: 2 günde bir
    val cycleGap: StateFlow<String> = _cycleGap.asStateFlow()
    // ----------------------------------------

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.allExercises.collectLatest { _allExercises.value = it }
        }
    }

    fun onNameChange(name: String) { _workoutName.value = name }

    // Planlama Fonksiyonları
    fun setScheduleType(type: ScheduleType) { _scheduleType.value = type }

    fun toggleDaySelection(dayIndex: Int) {
        if (selectedDays.contains(dayIndex)) selectedDays.remove(dayIndex)
        else selectedDays.add(dayIndex)
    }

    fun onCycleGapChange(gap: String) {
        // Sadece sayı girilmesini sağla
        if (gap.all { it.isDigit() }) {
            _cycleGap.value = gap
        }
    }

    fun toggleExerciseSelection(exercise: Exercise) {
        val exists = selectedExercises.find { it.exercise.exerciseId == exercise.exerciseId }
        if (exists != null) selectedExercises.remove(exists)
        else selectedExercises.add(SelectedExerciseState(exercise))
    }

    fun updateSetsReps(exerciseId: Int, sets: String, reps: String) {
        val index = selectedExercises.indexOfFirst { it.exercise.exerciseId == exerciseId }
        if (index != -1) {
            selectedExercises[index] = selectedExercises[index].copy(sets = sets, reps = reps)
        }
    }

    fun removeExercise(exerciseId: Int) {
        val index = selectedExercises.indexOfFirst { it.exercise.exerciseId == exerciseId }
        if (index != -1) selectedExercises.removeAt(index)
    }

    fun saveWorkout(onSuccess: () -> Unit) {
        if (_workoutName.value.isBlank() || selectedExercises.isEmpty()) return

        viewModelScope.launch {

            // 1. GÜNLERİ FORMATLA
            // UI'dan gelen liste (Örn: [1, 3]) -> Veritabanı formatına ("1,3") çeviriyoruz
            val daysString = selectedDays.sorted().joinToString(",")

            // 2. ANTRENMANI OLUŞTUR
            val newWorkout = WorkoutEntity(
                workoutName = _workoutName.value,
                difficulty = "Custom",
                duration = "${selectedExercises.size * 5} dk",

                // --- YENİ VERİLERİ EKLİYORUZ ---
                scheduleType = _scheduleType.value.name, // "WEEKLY" veya "CYCLIC"
                recurrenceDays = if (_scheduleType.value == ScheduleType.WEEKLY) daysString else "",
                recurrenceGap = if (_scheduleType.value == ScheduleType.CYCLIC) (_cycleGap.value.toIntOrNull() ?: 0) else 0
            )

            // 3. CROSS REF (Hareket İlişkileri) - Burası Aynı
            val crossRefs = selectedExercises.mapIndexed { index, item ->
                WorkoutExerciseCrossRef(
                    workoutId = 0,
                    exerciseId = item.exercise.exerciseId,
                    sets = item.sets.toIntOrNull() ?: 3,
                    reps = item.reps,
                    order = index
                )
            }

            // 4. KAYDET
            workoutRepository.createWorkout(newWorkout, crossRefs)
            onSuccess()
        }
    }
}