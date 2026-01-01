package com.example.gymapp002.data.repository

import com.example.gymapp002.data.local.dao.ExerciseDAO
import com.example.gymapp002.data.local.dao.WorkoutDao
import com.example.gymapp002.data.local.dao.WorkoutHistoryDao
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDAO,
    private val workoutHistoryDao: WorkoutHistoryDao
) {

    // --- MEVCUT ANTRENMAN YÖNETİMİ ---

    // Tüm antrenmanları getir
    val allWorkouts: Flow<List<WorkoutWithExercises>> = workoutDao.getWorkoutsWithExercises()

    // YENİ EKLENEN: Tek bir antrenmanı detaylarıyla getir (Detay sayfası için şart!)
    fun getWorkoutById(workoutId: Int): Flow<WorkoutWithExercises?> {
        return workoutDao.getWorkoutWithExercisesById(workoutId)
    }

    suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun insertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef) {
        workoutDao.insertWorkoutExerciseCrossRef(crossRef)
    }

    suspend fun deleteWorkout(workoutId: Int) {
        workoutDao.deleteWorkoutById(workoutId)
    }

    // --- DÜZELTİLEN FONKSİYON: createWorkout ---
    // Artık senin istediğin gibi detaylı 'List<WorkoutExerciseCrossRef>' alıyor.
    // 2. resimdeki type mismatch hatasını çözer.
    suspend fun createWorkout(workout: WorkoutEntity, crossRefs: List<WorkoutExerciseCrossRef>) {
        // 1. Antrenmanı kaydet ve oluşan gerçek ID'yi al
        val newWorkoutId = workoutDao.insertWorkout(workout).toInt()

        // 2. Listeyi dön ve her parçaya bu yeni ID'yi verip kaydet
        crossRefs.forEach { ref ->
            // ViewModel'de ID 0 geliyordu, burada gerçek ID ile güncelliyoruz
            workoutDao.insertWorkoutExerciseCrossRef(
                ref.copy(workoutId = newWorkoutId)
            )
        }
    }

    // --- GEÇMİŞ (HISTORY) YÖNETİMİ ---

    suspend fun logWorkout(history: WorkoutHistoryEntity) {
        workoutHistoryDao.insertHistory(history)
    }

    fun getLastWorkout(): Flow<WorkoutHistoryEntity?> {
        return workoutHistoryDao.getLastWorkout()
    }

    fun getWeeklyWorkoutCount(startDate: Long, endDate: Long): Flow<Int> {
        return workoutHistoryDao.getWorkoutCountByDateRange(startDate, endDate)
    }
}