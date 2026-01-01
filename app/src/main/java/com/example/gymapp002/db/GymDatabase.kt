package com.example.gymapp002.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymapp002.data.local.dao.ExerciseDAO
import com.example.gymapp002.data.local.dao.WorkoutDao
import com.example.gymapp002.data.local.dao.WorkoutHistoryDao
import com.example.gymapp002.data.local.entity.ExerciseEntity
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseCrossRef::class,
        WorkoutHistoryEntity::class // YENİ TABLO
    ],
    version = 5, // VERSİYON GÜNCELLENDİ
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDAO
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutHistoryDao(): WorkoutHistoryDao // YENİ DAO

    companion object {
        @Volatile
        private var Instance: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GymDatabase::class.java, "gym_database")
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration() // Versiyon değişince eski veriyi silip yeniden kurar
                    .build()
                    .also { Instance = it }
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(
                            database.exerciseDao(),
                            database.workoutDao()
                        )
                    }
                }
            }
        }

        // Başlangıç verilerini (Seed Data) yükleyen fonksiyon
        suspend fun populateDatabase(exerciseDao: ExerciseDAO, workoutDao: WorkoutDao) {
            // Temizlik (Garanti olsun diye)
            exerciseDao.deleteAll()
            workoutDao.deleteAll()

            // 1. DETAYLI EGZERSİZLERİ EKLE
            val exercises = listOf(
                ExerciseEntity(
                    name = "Bench Press",
                    muscleGroup = "Göğüs",
                    secondaryMuscles = "Ön Omuz, Triceps",
                    category = "Kuvvet",
                    difficulty = "Intermediate",
                    equipment = "Barbell, Bench",
                    description = "Sırt üstü sehpaya uzanın. Barı omuz genişliğinden biraz geniş tutun. Göğüs hizasına indirip nefes vererek itin.",
                    tips = "Dirseklerinizi 90 derece açmayın, hafifçe vücuda yaklaştırın. Ayaklarınız yere sağlam bassın."
                ),
                ExerciseEntity(
                    name = "Squat",
                    muscleGroup = "Bacak",
                    secondaryMuscles = "Kalça, Core",
                    category = "Kuvvet",
                    difficulty = "Advanced",
                    equipment = "Barbell, Rack",
                    description = "Barı sırtınıza (trapez kaslarına) yerleştirin. Ayakları omuz genişliğinde açın. Sandalyeye oturur gibi çömelin ve kalkın.",
                    tips = "Dizlerinizin içe çökmesine izin vermeyin. Sırtınızı dik tutun."
                ),
                ExerciseEntity(
                    name = "Deadlift",
                    muscleGroup = "Sırt",
                    secondaryMuscles = "Bacak, Kalça, Forearm",
                    category = "Kuvvet",
                    difficulty = "Advanced",
                    equipment = "Barbell",
                    description = "Barı kaval kemiğinize yaklaştırın. Kalçayı geriye atarak eğilin. Sırt düz bir şekilde barı yerden kaldırın.",
                    tips = "Belinizi asla bükmeyin (kambur çıkarmayın). Barı vücudunuza yakın tutun."
                ),
                ExerciseEntity(
                    name = "Dumbbell Curl",
                    muscleGroup = "Biceps",
                    secondaryMuscles = "Forearm",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Dumbbell",
                    description = "Ayakta dik durun. Avuç içleri karşıya bakacak şekilde dambılları omuz hizasına kaldırın ve yavaşça indirin.",
                    tips = "Vücudunuzu sallayarak momentum almayın. Sadece kollarınız hareket etsin."
                ),
                ExerciseEntity(
                    name = "Lat Pulldown",
                    muscleGroup = "Sırt",
                    secondaryMuscles = "Biceps",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Cable Machine",
                    description = "Barı geniş tutun, göğsünüze doğru çekerken kürek kemiklerinizi birbirine yaklaştırın.",
                    tips = "Geriye doğru aşırı yatmayın. Hareketi kontrolü yapın."
                ),
                ExerciseEntity(
                    name = "Push Up",
                    muscleGroup = "Göğüs",
                    secondaryMuscles = "Triceps, Omuz",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Vücut Ağırlığı",
                    description = "Plank pozisyonu alın. Göğsünüzü yere yaklaştırıp tekrar itin.",
                    tips = "Kalçayı düşürmeyin veya çok yukarı kaldırmayın."
                )
            )
            exerciseDao.insertAll(exercises)

            // 2. SİSTEM ANTRENMANLARINI EKLE (SİLİNEMEZ)

            // Full Body
            val fullBodyId = workoutDao.insertWorkout(
                WorkoutEntity(
                    workoutName = "Başlangıç: Full Body",
                    difficulty = "Beginner",
                    duration = "45 dk",
                    scheduleType = "WEEKLY",
                    isSystemWorkout = true,
                    description = "Tüm vücudu çalıştıran temel adaptasyon programı."
                )
            ).toInt()

            // Üst Vücut
            val upperBodyId = workoutDao.insertWorkout(
                WorkoutEntity(
                    workoutName = "Orta Seviye: Üst Vücut",
                    difficulty = "Intermediate",
                    duration = "60 dk",
                    scheduleType = "WEEKLY",
                    isSystemWorkout = true,
                    description = "Göğüs, Sırt, Omuz ve Kol odaklı antrenman."
                )
            ).toInt()

            // 3. ANTRENMAN İÇERİKLERİNİ OLUŞTUR (Cross Ref)
            // Not: ID'lerin 1'den başladığını varsayıyoruz.
            // 1:Bench, 2:Squat, 3:Deadlift, 4:Curl, 5:LatPulldown, 6:PushUp

            // Full Body Programı:
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 1, sets = 3, reps = "10", order = 1)) // Bench
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 2, sets = 3, reps = "12", order = 2)) // Squat
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 6, sets = 3, reps = "Max", order = 3)) // Push Up

            // Üst Vücut Programı:
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 1, sets = 4, reps = "8", order = 1)) // Bench
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 3, sets = 3, reps = "10", order = 2)) // Deadlift
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 5, sets = 3, reps = "12", order = 3)) // Lat Pulldown
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 4, sets = 3, reps = "12", order = 4)) // Curl
        }
    }
}