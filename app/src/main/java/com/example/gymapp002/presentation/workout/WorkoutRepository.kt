package com.example.gymapp002.data.repository

import com.example.gymapp002.data.local.dao.WorkoutDao
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow


class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // Antrenmanları ve içeriklerini getir
    val allWorkouts: Flow<List<WorkoutWithExercises>> = workoutDao.getWorkoutsWithExercises()


    // --- YENİ EKLENEN FONKSİYON ---
    fun getWorkoutById(id: Int): Flow<WorkoutWithExercises> {
        return workoutDao.getWorkoutWithExercisesById(id)
    }
    // -----------------------------

    // Yeni antrenman kaydet
    // Adım 1: Antrenman ismini kaydet -> ID al
    // Adım 2: O ID'yi kullanarak hareketleri bağla (CrossRef)
    suspend fun createWorkout(workout: WorkoutEntity, exercises: List<WorkoutExerciseCrossRef>) {
        val newWorkoutId = workoutDao.insertWorkout(workout)

        exercises.forEach { crossRef ->
            // Oluşan yeni ID'yi crossRef'e ata
            val finalCrossRef = crossRef.copy(workoutId = newWorkoutId.toInt())
            workoutDao.insertWorkoutExerciseCrossRef(finalCrossRef)
        }
    }

    //eklenen antrenmanların silinme özelliğini sağlar
    suspend fun deleteWorkout(workoutId: Int) {
        workoutDao.deleteWorkoutById(workoutId)
    }
}