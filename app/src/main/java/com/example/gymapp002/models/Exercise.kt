package com.example.gymapp002.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises"
)

data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Int,
    val name: String,
    val muscleGroup: String,
    val category: String,
    val difficulty: String,
    val recipe: String?,
    val description: String?,
    val desURL: String?,
    val equipment: String,
    val isFavorite: Boolean = false
)//belki buraya serialized gibi bir şey gelebilir
