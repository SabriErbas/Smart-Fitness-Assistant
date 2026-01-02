package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Int = 0,
    val workoutName: String,     // Örn: "Göğüs Günü"
    val date: LocalDate,         // Ne zaman yapıldı?
    val duration: String,        // Ne kadar sürdü? (Örn: "01:15")
    val totalVolume: Double,     // Toplam kaldırılan ağırlık (Fil hesabı için)
    val caloriesBurnt: Int = 0   // Tahmini kalori (Opsiyonel)
)