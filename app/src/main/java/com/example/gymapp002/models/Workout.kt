package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val workoutId: Int = 0,
    val workoutName: String,
    val difficulty: String,
    val duration: String,
    val scheduleType: String = "WEEKLY",
    val recurrenceDays: String = "",
    val recurrenceGap: Int = 0,

    // --- YENİ: SİLİNEMEZ SİSTEM ANTRENMANI MI? ---
    val isSystemWorkout: Boolean = false, // True ise kullanıcı bunu silemez
    val description: String = "" // Antrenmanın amacı ne? (Örn: "Yeni başlayanlar için tüm vücut adaptasyonu")
)