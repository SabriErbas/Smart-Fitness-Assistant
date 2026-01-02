package com.example.gymapp002.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_history_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutHistoryEntity::class,
            parentColumns = ["historyId"],
            childColumns = ["historyId"],
            onDelete = ForeignKey.CASCADE // Antrenman geçmişi silinirse detayları da silinsin
        )
    ]
)
data class WorkoutHistoryLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val historyId: Int,          // Hangi geçmiş kaydına ait?
    val exerciseName: String,    // Hareket adı
    val setNumber: Int,          // Set no
    val weight: Double,          // Kullanıcının girdiği kilo
    val reps: Int,               // Kullanıcının girdiği tekrar
    val isCompleted: Boolean     // Tamamlandı mı?
)