package com.example.gymapp002.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymapp002.data.local.dao.ExerciseDAO
import com.example.gymapp002.data.local.dao.WorkoutDao
import com.example.gymapp002.data.local.dao.WorkoutHistoryDao
import com.example.gymapp002.data.local.entity.ExerciseEntity as Exercise
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.data.local.entity.WorkoutExerciseCrossRef
import com.example.gymapp002.data.local.entity.WorkoutHistoryEntity
import com.example.gymapp002.data.local.entity.WorkoutHistoryLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        WorkoutEntity::class,
        WorkoutExerciseCrossRef::class,
        WorkoutHistoryEntity::class, // Geçmiş Özeti
        WorkoutHistoryLog::class     // EKSİK OLAN BUYDU: Set Detayları
    ],
    version = 6, // Versiyonu artırdık
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDAO
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutHistoryDao(): WorkoutHistoryDao

    companion object {
        @Volatile
        private var Instance: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GymDatabase::class.java, "gym_database")
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration() // Veritabanını sıfırlar ve yeniden kurar
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
            // Temizlik
            exerciseDao.deleteAll()
            workoutDao.deleteAll()

            // 1. ZENGİNLEŞTİRİLMİŞ EGZERSİZ LİSTESİ (12 Adet)
            val exercises = listOf(
                // --- GÖĞÜS ---
                Exercise(
                    name = "Bench Press",
                    muscleGroup = "Göğüs",
                    category = "Kuvvet",
                    difficulty = "Intermediate",
                    equipment = "Barbell, Bench",
                    description = "Sırt üstü sehpaya uzanın. Barı göğüs hizasına indirip itin.",
                    isGameEnabled = false // Tehlikeli, oyun yok
                ),
                Exercise(
                    name = "Incline Dumbbell Press",
                    muscleGroup = "Göğüs",
                    category = "Kuvvet",
                    difficulty = "Intermediate",
                    equipment = "Dumbbell, Incline Bench",
                    description = "Eğimli sehpada dambılları yukarı doğru itin.",
                    isGameEnabled = false
                ),

                // --- SIRT ---
                Exercise(
                    name = "Deadlift",
                    muscleGroup = "Sırt",
                    category = "Kuvvet",
                    difficulty = "Advanced",
                    equipment = "Barbell",
                    description = "Barı kaval kemiğinize yaklaştırın ve sırt düz şekilde kaldırın.",
                    isGameEnabled = false
                ),
                Exercise(
                    name = "Lat Pulldown",
                    muscleGroup = "Sırt",
                    category = "Makine",
                    difficulty = "Beginner",
                    equipment = "Cable Machine",
                    description = "Barı geniş tutun, göğsünüze doğru çekin.",
                    isGameEnabled = true, // OYUN VAR 🎮 (Makine olduğu için güvenli)
                    gameSensitivity = 1.0f
                ),

                // --- BACAK ---
                Exercise(
                    name = "Squat",
                    muscleGroup = "Bacak",
                    category = "Kuvvet",
                    difficulty = "Advanced",
                    equipment = "Barbell, Rack",
                    description = "Barı sırta alıp çömelip kalkın.",
                    isGameEnabled = false
                ),
                Exercise(
                    name = "Leg Extension",
                    muscleGroup = "Bacak",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Machine",
                    description = "Oturarak bacaklarınızı düzleşene kadar yukarı kaldırın.",
                    isGameEnabled = true, // OYUN VAR 🎮 (Mükemmel oyun hareketi)
                    gameSensitivity = 0.8f
                ),

                // --- OMUZ ---
                Exercise(
                    name = "Overhead Press",
                    muscleGroup = "Omuz",
                    category = "Kuvvet",
                    difficulty = "Intermediate",
                    equipment = "Barbell",
                    description = "Barı omuz hizasından başınızın üzerine itin.",
                    isGameEnabled = false
                ),
                Exercise(
                    name = "Lateral Raise",
                    muscleGroup = "Omuz",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Dumbbell",
                    description = "Dambılları yanlara doğru omuz hizasına kadar açın.",
                    isGameEnabled = true, // OYUN VAR 🎮 (Kolları kanat gibi çırpma oyunu olabilir)
                    gameSensitivity = 1.5f
                ),

                // --- KOL (BICEPS/TRICEPS) ---
                Exercise(
                    name = "Dumbbell Curl",
                    muscleGroup = "Biceps",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Dumbbell",
                    description = "Avuç içleri karşıya bakacak şekilde dambılları kaldırın.",
                    isGameEnabled = true, // OYUN VAR 🎮
                    gameSensitivity = 1.2f
                ),
                Exercise(
                    name = "Triceps Pushdown",
                    muscleGroup = "Triceps",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Cable Machine",
                    description = "Kabloları aşağı doğru iterek kollarınızı düzleştirin.",
                    isGameEnabled = true, // OYUN VAR 🎮
                    gameSensitivity = 1.0f
                )
            )

            // Hepsini ekle
            exercises.forEach { exerciseDao.insertExercise(it) }

            // 2. HAZIR ANTRENMAN PROGRAMLARI

            // Program 1: Full Body (Başlangıç)
            val fullBodyId = workoutDao.insertWorkout(
                WorkoutEntity(
                    workoutName = "🔥 Full Body Başlangıç",
                    difficulty = "Beginner",
                    duration = "45 dk",
                    scheduleType = "WEEKLY",
                    recurrenceDays = "1,3,5", // Pzt, Çar, Cuma
                    isSystemWorkout = true,
                    description = "Tüm vücudu çalıştıran temel adaptasyon programı."
                )
            ).toInt()

            // Program 2: Üst Vücut (Oyunlu)
            val upperBodyId = workoutDao.insertWorkout(
                WorkoutEntity(
                    workoutName = "🎮 Üst Vücut & Oyun",
                    difficulty = "Intermediate",
                    duration = "60 dk",
                    scheduleType = "WEEKLY",
                    recurrenceDays = "2,4", // Salı, Perşembe
                    isSystemWorkout = true,
                    description = "Hem kas yap hem eğlen! Oyun destekli hareketler içerir."
                )
            ).toInt()

            // Program 3: Bacak Günü (Zor)
            val legDayId = workoutDao.insertWorkout(
                WorkoutEntity(
                    workoutName = "💀 Legendary Leg Day",
                    difficulty = "Advanced",
                    duration = "50 dk",
                    scheduleType = "ONCE",
                    isSystemWorkout = true,
                    description = "Yürümekte zorlanacağın o gün."
                )
            ).toInt()

            // 3. ANTRENMAN İÇERİKLERİ (ID'ler ekleme sırasına göredir: 1'den başlar)

            // --- Full Body İçeriği ---
            // 1:Bench, 5:Squat, 4:LatPulldown(Oyun), 9:Curl(Oyun)
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 1, sets = 3, reps = "10", order = 1))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 5, sets = 3, reps = "12", order = 2))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 4, sets = 3, reps = "12", order = 3))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = fullBodyId, exerciseId = 9, sets = 3, reps = "12", order = 4))

            // --- Üst Vücut & Oyun İçeriği ---
            // 2:Incline, 8:Lateral(Oyun), 9:Curl(Oyun), 10:Triceps(Oyun)
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 2, sets = 3, reps = "10", order = 1))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 8, sets = 4, reps = "15", order = 2))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 9, sets = 3, reps = "12", order = 3))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = upperBodyId, exerciseId = 10, sets = 3, reps = "12", order = 4))

            // --- Leg Day İçeriği ---
            // 5:Squat, 3:Deadlift, 6:LegExt(Oyun)
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = legDayId, exerciseId = 5, sets = 4, reps = "8", order = 1))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = legDayId, exerciseId = 3, sets = 3, reps = "6", order = 2))
            workoutDao.insertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = legDayId, exerciseId = 6, sets = 4, reps = "15", order = 3)) // Yakan set
        }
    }
}