package com.example.gymapp002.data.local.entity

import androidx.room.Embedded

// Bu sınıf veritabanında bir tablo değil, sadece bir sorgu sonucudur.
// Egzersiz bilgisi + O antrenmana özel set/tekrar/sıra bilgisi
data class WorkoutDetailItem(
    @Embedded val exercise: ExerciseEntity,
    val sets: Int,
    val reps: String,
    val order: Int
)