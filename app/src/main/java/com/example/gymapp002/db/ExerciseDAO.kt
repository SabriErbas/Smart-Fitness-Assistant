package com.example.gymapp002.data.local.dao


import com.example.gymapp002.data.local.entity.ExerciseEntity as Exercise
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseDAO {
    // Tüm egzersizleri getir (Flow sayesinde anlık güncellenir)
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    // İsime veya kasa göre arama yap (SearchScreen için)
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' OR muscleGroup LIKE '%' || :query || '%'")
    fun searchExercises(query: String): Flow<List<Exercise>>

    // Yeni egzersiz ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    // Çoklu ekleme (Uygulama ilk açıldığında veritabanını doldurmak için)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    // Silme
    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()


}