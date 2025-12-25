package com.example.gymapp002.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gymapp002.GymApplication
import com.example.gymapp002.data.repository.ExerciseRepository
// Aşağıdaki importları eklediğinden emin ol (Kırmızı yanarsa Alt+Enter yap)
import com.example.gymapp002.data.repository.WorkoutRepository
import com.example.gymapp002.ui.screens.CreateWorkoutViewModel
import com.example.gymapp002.ui.screens.SearchViewModel
import com.example.gymapp002.ui.screens.WorkoutViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // 1. SearchViewModel Tarifi (Zaten Vardı)
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            val repository = ExerciseRepository(application.database.exerciseDao())
            SearchViewModel(repository)
        }

        // 2. CreateWorkoutViewModel Tarifi (EKSİK OLAN BUYDU! 👇)
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)

            // ViewModel iki tane Repository istiyor, onları hazırlayıp veriyoruz:
            CreateWorkoutViewModel(
                exerciseRepository = ExerciseRepository(application.database.exerciseDao()),
                workoutRepository = WorkoutRepository(application.database.workoutDao())
            )
        }

        // 3. WorkoutViewModel (YENİ EKLENEN)
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            WorkoutViewModel(
                workoutRepository = WorkoutRepository(app.database.workoutDao())
            )
        }
    }
}