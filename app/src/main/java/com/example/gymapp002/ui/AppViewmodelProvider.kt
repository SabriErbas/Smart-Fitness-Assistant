package com.example.gymapp002.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gymapp002.GymApplication
import com.example.gymapp002.data.repository.ExerciseRepository
import com.example.gymapp002.data.repository.WorkoutRepository
import com.example.gymapp002.ui.screens.CreateWorkoutViewModel
import com.example.gymapp002.ui.screens.HomeViewModel
import com.example.gymapp002.ui.screens.ProfileViewModel
import com.example.gymapp002.ui.screens.SearchViewModel
import com.example.gymapp002.ui.screens.WorkoutDetailViewModel
import com.example.gymapp002.ui.screens.WorkoutViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // 1. SearchViewModel
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            val repository = ExerciseRepository(app.database.exerciseDao())
            SearchViewModel(repository)
        }

        // 2. CreateWorkoutViewModel
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            CreateWorkoutViewModel(
                exerciseRepository = ExerciseRepository(app.database.exerciseDao()),
                // GÜNCELLENDİ: Artık 3 parametre alıyor
                workoutRepository = WorkoutRepository(
                    workoutDao = app.database.workoutDao(),
                    exerciseDao = app.database.exerciseDao(),
                    workoutHistoryDao = app.database.workoutHistoryDao()
                )
            )
        }

        // 3. WorkoutViewModel
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            WorkoutViewModel(
                // GÜNCELLENDİ: Artık 3 parametre alıyor
                workoutRepository = WorkoutRepository(
                    workoutDao = app.database.workoutDao(),
                    exerciseDao = app.database.exerciseDao(),
                    workoutHistoryDao = app.database.workoutHistoryDao()
                )
            )
        }

        // 4. WorkoutDetailViewModel
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            WorkoutDetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                // GÜNCELLENDİ: Artık 3 parametre alıyor
                workoutRepository = WorkoutRepository(
                    workoutDao = app.database.workoutDao(),
                    exerciseDao = app.database.exerciseDao(),
                    workoutHistoryDao = app.database.workoutHistoryDao()
                )
            )
        }

        // 5. HomeViewModel
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
            HomeViewModel(
                // GÜNCELLENDİ: Artık 3 parametre alıyor
                workoutRepository = WorkoutRepository(
                    workoutDao = app.database.workoutDao(),
                    exerciseDao = app.database.exerciseDao(),
                    workoutHistoryDao = app.database.workoutHistoryDao()
                )
            )
        }

        // 6. ProfileViewModel
        initializer {
            ProfileViewModel()
        }
    }
}