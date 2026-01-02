package com.example.gymapp002.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import com.example.gymapp002.data.local.entity.WorkoutHistoryLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkoutHistoryDao {

    // 1. Antrenman özetini kaydet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: WorkoutHistoryEntity): Long

    // 2. Set detaylarını kaydet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<WorkoutHistoryLog>)

    // 3. Tüm geçmişi getir
    @Query("SELECT * FROM workout_history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<WorkoutHistoryEntity>>

    // --- EKSİK OLANLAR EKLENDİ ---

    // 4. Son antrenmanı getir (Home ekranı için)
    @Query("SELECT * FROM workout_history ORDER BY date DESC, historyId DESC LIMIT 1")
    fun getLastWorkout(): Flow<WorkoutHistoryEntity?>

    // 5. Haftalık antrenman sayısı (Grafik için)
    @Query("SELECT COUNT(*) FROM workout_history WHERE date >= :startDate AND date <= :endDate")
    fun getWorkoutCountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int>

    // 6. Toplam Tonaj (Fil Hesabı için)
    @Query("SELECT SUM(totalVolume) FROM workout_history")
    fun getTotalLifetimeVolume(): Flow<Double?>
}