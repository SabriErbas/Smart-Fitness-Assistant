package com.example.gymapp002.data.repository

import com.example.gymapp002.data.local.dao.ExerciseDAO
import com.example.gymapp002.data.local.dao.WorkoutDao
import com.example.gymapp002.data.local.dao.WorkoutHistoryDao
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import com.example.gymapp002.data.local.entity.WorkoutHistoryLog
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant // YENİ IMPORT
import java.time.LocalDate
import java.time.ZoneId   // YENİ IMPORT

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDAO,
    private val workoutHistoryDao: WorkoutHistoryDao
) {

    // --- MEVCUT ANTRENMAN YÖNETİMİ ---

    // Tüm antrenmanları getir
    val allWorkouts: Flow<List<WorkoutWithExercises>> = workoutDao.getWorkoutsWithExercises()

    // Tek bir antrenmanı detaylarıyla getir
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

    // Antrenman oluşturma
    suspend fun createWorkout(workout: WorkoutEntity, crossRefs: List<WorkoutExerciseCrossRef>) {
        // 1. Antrenmanı kaydet ve oluşan gerçek ID'yi al
        val newWorkoutId = workoutDao.insertWorkout(workout).toInt()

        // 2. Listeyi dön ve her parçaya bu yeni ID'yi verip kaydet
        crossRefs.forEach { ref ->
            workoutDao.insertWorkoutExerciseCrossRef(
                ref.copy(workoutId = newWorkoutId)
            )
        }
    }

    // --- GEÇMİŞ (HISTORY) YÖNETİMİ ---

    suspend fun logWorkout(history: WorkoutHistoryEntity) {
        workoutHistoryDao.insertHistory(history)
    }

    // --- FAZ 4: DETAY VE SIRALAMA ---

    // Detayları sıralı getir
    fun getWorkoutDetails(workoutId: Int): Flow<List<WorkoutDetailItem>> {
        return workoutDao.getWorkoutDetailItems(workoutId)
    }

    // Sıralamayı veritabanında güncelle
    suspend fun swapExercisesOrder(workoutId: Int, item1: WorkoutDetailItem, item2: WorkoutDetailItem) {
        workoutDao.updateExerciseOrder(workoutId, item1.exercise.exerciseId, item2.order)
        workoutDao.updateExerciseOrder(workoutId, item2.exercise.exerciseId, item1.order)
    }

    // Son antrenmanı getir
    fun getLastWorkout(): Flow<WorkoutHistoryEntity?> {
        return workoutHistoryDao.getLastWorkout()
    }

    // --- DÜZELTİLEN FONKSİYON BURASI ---
    // Haftalık antrenman sayısını getir
    fun getWeeklyWorkoutCount(startDateMillis: Long, endDateMillis: Long): Flow<Int> {
        // Milisaniyeyi (Long) -> Tarihe (LocalDate) güvenli çeviriyoruz
        val start = java.time.Instant.ofEpochMilli(startDateMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        val end = java.time.Instant.ofEpochMilli(endDateMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        return workoutHistoryDao.getWorkoutCountByDateRange(start, end)
    }

    // Antrenman geçmişini ve detaylarını kaydetme
    suspend fun saveWorkoutSession(history: WorkoutHistoryEntity, logs: List<WorkoutHistoryLog>) {
        val historyId = workoutHistoryDao.insertHistory(history)
        // Dönen ID'yi loglara ata
        val logsWithId = logs.map { it.copy(historyId = historyId.toInt()) }
        workoutHistoryDao.insertLogs(logsWithId)
    }

    // Toplam kaldırılan ağırlığı getir
    fun getTotalVolume(): Flow<Double> {
        // null gelirse 0.0 döndür
        return workoutHistoryDao.getTotalLifetimeVolume().map { it ?: 0.0 }
    }
}