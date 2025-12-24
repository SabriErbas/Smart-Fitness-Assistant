package com.example.gymapp002.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymapp002.models.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Exercise::class],
    version = 1
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDAO


    companion object {
        @Volatile
        private var Instance: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GymDatabase::class.java, "gym_database")
                    // Veritabanı ilk oluştuğunda içine varsayılan verileri atmak için Callback ekliyoruz
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

        // Uygulama ilk yüklendiğinde boş gelmesin, içine 3-5 hareket atalım.
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.exerciseDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(exerciseDao: ExerciseDAO) {
            // Başlangıç verileri
            val initialData = listOf(
                // 1. Göğüs - Barbell
                Exercise(
                    id = 0,
                    name = "Bench Press",
                    muscleGroup = "Göğüs",
                    category = "Kuvvet",
                    difficulty = "Intermediate",
                    equipment = "Barbell",
                    recipe = "Sırt üstü sehpaya uzanın, barı göğüs hizasına indirip nefes vererek yukarı itin.",
                    description = "Göğüs kaslarını geliştirmek için en temel ve etkili egzersizdir.",
                    desURL = null
                ),

                // 2. Bacak - Vücut Ağırlığı/Dumbbell
                Exercise(
                    id = 0,
                    name = "Goblet Squat",
                    muscleGroup = "Bacak",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Dumbbell",
                    recipe = "Dumbbell'ı göğüs hizasında tutun, sırt düz bir şekilde çömelin ve kalkın.",
                    description = "Ön bacak (kavriceps) ve kalça kaslarını çalıştırır, form öğrenmek için harikadır.",
                    desURL = null
                ),

                // 3. Sırt - Makine
                Exercise(
                    id = 0,
                    name = "Lat Pulldown",
                    muscleGroup = "Sırt",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Cable Machine",
                    recipe = "Barı geniş tutun, göğsünüze doğru çekerken kürek kemiklerinizi sıkıştırın.",
                    description = "Sırt genişliğini artırmak ve kanat kaslarını geliştirmek için idealdir.",
                    desURL = null
                ),

                // 4. Omuz - Dumbbell
                Exercise(
                    id = 0,
                    name = "Lateral Raise",
                    muscleGroup = "Omuz",
                    category = "Hipertrofi",
                    difficulty = "Intermediate",
                    equipment = "Dumbbell",
                    recipe = "Ayakta durun, dirsekleri hafif kırarak ağırlıkları yanlara doğru omuz hizasına kadar kaldırın.",
                    description = "Omuz başlarını yuvarlaklaştırmak ve geniş göstermek için izole bir harekettir.",
                    desURL = null
                ),

                // 5. Arka Kol (Triceps) - Cable
                Exercise(
                    id = 0,
                    name = "Tricep Pushdown",
                    muscleGroup = "Arka Kol",
                    category = "İzolasyon",
                    difficulty = "Beginner",
                    equipment = "Cable Machine",
                    recipe = "Dirsekleri vücuda sabitleyin, ipi veya barı aşağı doğru iterek kolunuzu düzleştirin.",
                    description = "Arka kol kaslarını izole etmek ve sıkılaştırmak için kullanılır.",
                    desURL = null
                ),

                // 6. Ön Kol (Biceps) - Barbell
                Exercise(
                    id = 0,
                    name = "Barbell Curl",
                    muscleGroup = "Ön Kol",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Barbell",
                    recipe = "Barı omuz genişliğinde tutun, dirsekleri oynatmadan ağırlığı yukarı kaldırın.",
                    description = "Biceps kaslarını hacimlendirmek için en popüler egzersizdir.",
                    desURL = null
                ),

                // 7. Tüm Vücut / Arka Zincir - Barbell
                Exercise(
                    id = 0,
                    name = "Deadlift",
                    muscleGroup = "Sırt/Bacak",
                    category = "Kuvvet",
                    difficulty = "Advanced",
                    equipment = "Barbell",
                    recipe = "Barı yerden kalça ve bacak gücüyle kaldırın, belinizi daima düz tutun.",
                    description = "Vücuttaki en çok kas grubunu aynı anda çalıştıran temel güç hareketidir.",
                    desURL = null
                ),

                // 8. Karın - Vücut Ağırlığı
                Exercise(
                    id = 0,
                    name = "Plank",
                    muscleGroup = "Karın",
                    category = "Dayanıklılık",
                    difficulty = "Beginner",
                    equipment = "Mat",
                    recipe = "Dirsekler ve ayak parmakları üzerinde vücudu düz bir çizgi halinde sabit tutun.",
                    description = "Core (merkez) bölgesini güçlendirmek ve stabiliteyi artırmak için yapılır.",
                    desURL = null
                ),

                // 9. Bacak / Kalça - Makine
                Exercise(
                    id = 0,
                    name = "Leg Press",
                    muscleGroup = "Bacak",
                    category = "Kuvvet",
                    difficulty = "Beginner",
                    equipment = "Machine",
                    recipe = "Koltuğa oturun, ayaklarınızı platforma yerleştirin ve platformu bacaklarınızla itin.",
                    description = "Bel problemi yaşayanlar için Squat'a alternatif güvenli bir bacak egzersizidir.",
                    desURL = null
                ),

                // 10. Kardiyo - Ekipman
                Exercise(
                    id = 0,
                    name = "Rowing Machine",
                    muscleGroup = "Tüm Vücut",
                    category = "Kardiyo",
                    difficulty = "Intermediate",
                    equipment = "Rowing Machine",
                    recipe = "Bacaklarla itiş yaparken aynı anda kulpu karnınıza doğru çekin.",
                    description = "Hem kondisyonu artırır hem de sırt ve bacak kaslarını aktif çalıştırır.",
                    desURL = null
                ))
            exerciseDao.insertAll(initialData)
        }
    }
}