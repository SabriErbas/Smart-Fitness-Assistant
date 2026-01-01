package com.example.gymapp002.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutHistoryDao {
    // Antrenman bitince kaydet
    @Insert
    suspend fun insertHistory(history: WorkoutHistoryEntity)

    // Son yapılan antrenmanı getir (Fil/Tank kartı için)
    @Query("SELECT * FROM workout_history ORDER BY date DESC LIMIT 1")
    fun getLastWorkout(): Flow<WorkoutHistoryEntity?>

    // Belirli iki tarih arasındaki antrenman sayısını getir (Haftalık Bar için)
    @Query("SELECT COUNT(*) FROM workout_history WHERE date >= :startDate AND date <= :endDate")
    fun getWorkoutCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    // Tüm geçmişi getir (History ekranı için - İleride lazım olacak)
    @Query("SELECT * FROM workout_history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<WorkoutHistoryEntity>>
}