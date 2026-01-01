package com.example.gymapp002.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat

data class ProfileUiState(
    val name: String = "Mühendis Bey",
    val title: String = "Computer Engineer",
    val weight: String = "80",
    val height: String = "180",
    val bmi: Double = 0.0,
    val bmiStatus: String = ""
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        calculateBMI()
    }

    // Kullanıcı veriyi değiştirdikçe burası çalışacak
    fun updateWeight(newWeight: String) {
        _uiState.value = _uiState.value.copy(weight = newWeight)
        calculateBMI()
    }

    fun updateHeight(newHeight: String) {
        _uiState.value = _uiState.value.copy(height = newHeight)
        calculateBMI()
    }

    private fun calculateBMI() {
        val weight = _uiState.value.weight.toDoubleOrNull() ?: 0.0
        val height = _uiState.value.height.toDoubleOrNull() ?: 0.0

        if (weight > 0 && height > 0) {
            // Boyu cm'den metreye çevir (180 -> 1.80)
            val heightInMeters = height / 100
            val bmiValue = weight / (heightInMeters * heightInMeters)

            // Durum Belirleme
            val status = when {
                bmiValue < 18.5 -> "Zayıf"
                bmiValue < 25.0 -> "Normal"
                bmiValue < 30.0 -> "Fazla Kilolu"
                else -> "Obez"
            }

            _uiState.value = _uiState.value.copy(
                bmi = bmiValue,
                bmiStatus = status
            )
        }
    }
}