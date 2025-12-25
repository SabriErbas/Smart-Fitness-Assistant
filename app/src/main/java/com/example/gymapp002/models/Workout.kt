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

    // --- YENİ EKLENEN SÜTUNLAR ---

    // Planlama Tipi: "WEEKLY" (Haftalık) veya "CYCLIC" (Döngüsel)
    val scheduleType: String = "WEEKLY",

    // Haftalık ise günler: "1,3,5" şeklinde String olarak tutacağız (Parsing kolay olsun diye)
    // 1=Pazartesi, 7=Pazar
    val recurrenceDays: String = "",

    // Döngüsel ise boşluk: "2" (2 günde bir)
    val recurrenceGap: Int = 0
)