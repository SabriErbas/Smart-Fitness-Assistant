package com.example.gymapp002.data.local.dao

import androidx.room.*
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
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


    @Query("DELETE FROM workouts")
    suspend fun deleteAll()

    // NOT: Eğer çapraz tabloyu (CrossRef) da temizlemek istersen
    // buraya ekleyebiliriz ama şimdilik hata gitmesi için bu yeterli.


    // 1. Antrenman Detaylarını (Set, Reps, Order dahil) Sıralı Getir
    @Query("""
        SELECT e.*, w.sets, w.reps, w.`order`
        FROM exercises e
        INNER JOIN workout_exercise_cross_ref w ON e.exerciseId = w.exerciseId
        WHERE w.workoutId = :workoutId
        ORDER BY w.`order` ASC
    """)
    fun getWorkoutDetailItems(workoutId: Int): Flow<List<WorkoutDetailItem>>

    // 2. Sıralama güncellemek için CrossRef'i güncellememiz lazım
    // Sadece order'ı değiştirmek için pratik bir query
    @Query("UPDATE workout_exercise_cross_ref SET `order` = :newOrder WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun updateExerciseOrder(workoutId: Int, exerciseId: Int, newOrder: Int)
}