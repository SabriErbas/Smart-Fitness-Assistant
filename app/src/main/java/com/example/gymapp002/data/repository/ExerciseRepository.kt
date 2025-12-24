package com.example.gymapp002.data.repository


import com.example.gymapp002.db.ExerciseDAO
import com.example.gymapp002.models.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDAO) {

    // Neden Flow?
    // Veritabanında bir değişiklik olduğunda (yeni veri eklendiğinde) 
    // UI'ın otomatik haberdar olması için "akan veri" (stream) kullanıyoruz.
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun searchExercises(query: String): Flow<List<Exercise>> {
        return exerciseDao.searchExercises(query)
    }

    // suspend: Bu işlem uzun sürebilir, ana ekranı dondurma (arka planda yap).
    suspend fun insert(exercise: Exercise) {
        exerciseDao.insertExercise(exercise)
    }
}