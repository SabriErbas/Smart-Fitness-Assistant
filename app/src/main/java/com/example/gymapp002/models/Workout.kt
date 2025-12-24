package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val workoutId: Int = 0,
    val workoutName: String, // Örn: "Pazartesi: Göğüs & Ön Kol"
    val difficulty: String,  // Örn: "Advanced"
    val duration: String     // Örn: "45 dk"
)