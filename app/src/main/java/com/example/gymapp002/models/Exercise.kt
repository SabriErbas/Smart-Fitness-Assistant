package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val difficulty: String,
    val category: String,
    val description: String,

    // --- YENİ EKLENEN ALANLAR ---
    val isGameEnabled: Boolean = false,       // Bu harekette oyun var mı?
    val idealWaveform: String = "",           // Python'dan çıkan dizi (Örn: "0.1,0.5,0.9...") String olarak tutalım
    val gameSensitivity: Float = 1.0f         // Oyun hızı çarpanı
)