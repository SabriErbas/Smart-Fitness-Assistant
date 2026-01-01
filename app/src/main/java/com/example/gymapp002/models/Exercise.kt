package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Int = 0,
    val name: String,
    val muscleGroup: String, // Örn: Göğüs, Sırt, Bacak
    val secondaryMuscles: String = "", // Örn: Triceps, Ön Omuz (Virgülle ayrılmış)
    val category: String,    // Kuvvet, Kardiyo, Esneme
    val difficulty: String,  // Beginner, Intermediate, Advanced
    val equipment: String,   // Barbell, Dumbbell, Machine, Bodyweight

    // --- YENİ EKLENEN DETAY ALANLARI ---
    val description: String, // Detaylı "Nasıl yapılır?" metni
    val gifUrl: String? = null, // İnternet URL'si veya local resource ismi (şimdilik null geçebiliriz)
    val tips: String = "",   // "Belini dik tut" gibi ipuçları
    val videoUrl: String? = null // Youtube linki vs.
)
// Artık sadece basit bir veri değil, dolu dolu bir ansiklopedi maddesi gibi.