package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Int = 0,
    val workoutId: Int,       // Hangi antrenmandı?
    val workoutName: String,  // İsmi neydi?
    val date: Long,           // Ne zaman bitti? (Milisaniye cinsinden tarih)
    val totalVolume: Int,     // Toplam kaç kg kaldırdın? (Fil hesabı için)
    val durationSeconds: Long // Kaç saniye sürdü?
)