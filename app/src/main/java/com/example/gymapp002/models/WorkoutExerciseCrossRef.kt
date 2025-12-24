package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.Index

// primaryKeys: Aynı antrenmanda aynı hareketin iki kere eklenmesini engeller.
// indices: Sorgu performansını artırır.
@Entity(
    tableName = "workout_exercise_cross_ref",
    primaryKeys = ["workoutId", "exerciseId"],
    indices = [Index(value = ["exerciseId"])]
)
data class WorkoutExerciseCrossRef(
    val workoutId: Int,
    val exerciseId: Int,
    val sets: Int,        // Örn: 4
    val reps: String,     // Örn: "8-12" veya "Fail"
    val order: Int        // Antrenman sırası (1. hareket, 2. hareket...)
)