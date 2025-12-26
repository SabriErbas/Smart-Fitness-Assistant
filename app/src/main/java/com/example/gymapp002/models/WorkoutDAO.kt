package com.example.gymapp002.data.local.dao

import androidx.room.*
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Yeni bir antrenman programı oluştur (Örn: "Bacak Günü")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long // Oluşan ID'yi geri döner

    // Antrenmana hareket ekle (Köprü tablosuna kayıt atar)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    //takvim üzerinde atanan ve eklenen antrenmanın silinmesini sağlar
    @Query("DELETE FROM workouts WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutById(workoutId: Int)

    // Antrenmanları ve içindeki hareketleri getir
    @Transaction // İlişkisel sorgularda @Transaction şarttır!
    @Query("SELECT * FROM workouts")
    fun getWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    // Sadece belirli bir antrenmanı getir
    @Transaction
    @Query("SELECT * FROM workouts WHERE workoutId = :workoutId")
    fun getWorkoutWithExercisesById(workoutId: Int): Flow<WorkoutWithExercises>
}