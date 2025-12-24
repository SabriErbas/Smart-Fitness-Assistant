package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mock Data Class
data class ExerciseItem(
    val id: Int,
    val name: String,
    val muscleGroup: String,
    val difficulty: String // Beginner, Intermediate, Advanced
)

@Composable
fun SearchScreen() {
    // Arama metni durumu
    var searchQuery by remember { mutableStateOf("") }

    // Mock Data - 10 Egzersiz
    val allExercises = remember {
        listOf(
            ExerciseItem(1, "Arnold Press", "Omuz", "Intermediate"),
            ExerciseItem(2, "Barbell Squat", "Bacak", "Advanced"),
            ExerciseItem(3, "Bench Press", "Göğüs", "Intermediate"),
            ExerciseItem(4, "Deadlift", "Sırt/Bacak", "Advanced"),
            ExerciseItem(5, "Dumbbell Curl", "Pazı (Biceps)", "Beginner"),
            ExerciseItem(6, "Face Pull", "Arka Omuz", "Intermediate"),
            ExerciseItem(7, "Lat Pulldown", "Sırt", "Beginner"),
            ExerciseItem(8, "Leg Extension", "Ön Bacak", "Beginner"),
            ExerciseItem(9, "Plank", "Karın (Core)", "Beginner"),
            ExerciseItem(10, "Triceps Pushdown", "Arka Kol", "Beginner")
        )
    }

    // Arama ve Sıralama Mantığı
    val filteredExercises = allExercises
        .filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.muscleGroup.contains(searchQuery, ignoreCase = true)
        }
        .sortedBy { it.name } // Alfabetik Sıralama

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // BlackBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. BAŞLIK
            Text(
                text = "Egzersiz Bul",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. ARAMA ÇUBUĞU (KAMERA İKONLU)
            SearchBarWithCamera(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onCameraClick = { /* İleride Makine Tanıma sistemini buraya bağlayacağız */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. EGZERSİZ LİSTESİ
            Text(
                text = "Tüm Egzersizler (${filteredExercises.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredExercises) { exercise ->
                    ExerciseRowCard(exercise)
                }
            }
        }
    }
}

// --- BİLEŞENLER (COMPONENTS) ---

@Composable
fun SearchBarWithCamera(
    query: String,
    onQueryChange: (String) -> Unit,
    onCameraClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Egzersiz adı ara...", color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary, // Odaklanınca Yeşil Çerçeve
            unfocusedBorderColor = Color.DarkGray,                  // Normalde Gri Çerçeve
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface, // Koyu Zemin
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            // KAMERA BUTONU - AI TARAMA
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "AI Camera Scan",
                    tint = MaterialTheme.colorScheme.primary // ASİT YEŞİLİ (Dikkat çeksin diye)
                )
            }
        }
    )
}

@Composable
fun ExerciseRowCard(exercise: ExerciseItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Egzersiz detayına git */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // DarkSurface (#232A2E)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Egzersiz Görseli (Placeholder)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search, // Geçici ikon
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Egzersiz Bilgileri
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exercise.muscleGroup,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary // Kas grubu Yeşil olsun
                    )
                    Text(
                        text = " • ${exercise.difficulty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Sağ Ok İkonu
            Icon(
                /*bu icon zaman içinde değişecek şimdilik bu şekilde*/
                imageVector = Icons.Default.Star,
                contentDescription = "Detail",
                tint = Color.Gray
            )
        }
    }
}