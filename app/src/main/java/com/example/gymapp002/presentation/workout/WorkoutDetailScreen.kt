package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.ExerciseEntity
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.ui.components.ExerciseInfoDialog // Import Eklendi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    onBackClick: () -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val workoutDetail by viewModel.workoutDetails.collectAsState()

    // YENİ: Seçili egzersizi tutan state (Null ise dialog kapalı)
    var selectedExercise by remember { mutableStateOf<ExerciseEntity?>(null) }

    // DİALOG KONTROLÜ
    if (selectedExercise != null) {
        ExerciseInfoDialog(
            exercise = selectedExercise!!,
            onDismiss = { selectedExercise = null }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Antrenman Detayı", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Antrenman Modu (Sonraki Faz) */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                text = { Text("BAŞLAT", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        workoutDetail?.let { detail ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                WorkoutHeader(detail)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Egzersizler (${detail.exercises.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(detail.exercises) { exercise ->
                        DetailExerciseCard(
                            exercise = exercise,
                            // Tıklanınca state'i güncelle
                            onClick = { selectedExercise = exercise }
                        )
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun WorkoutHeader(detail: WorkoutWithExercises) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = detail.workout.workoutName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Eğer açıklama varsa göster (Yeni özellik)
            if (detail.workout.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detail.workout.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = detail.workout.duration, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "•  ${detail.workout.difficulty}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DetailExerciseCard(
    exercise: ExerciseEntity,
    onClick: () -> Unit // Tıklama özelliği eklendi
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() } // Tıklanabilir alan
            .padding(12.dp)
    ) {
        // Görsel / Numara Alanı
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = exercise.name.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(exercise.muscleGroup, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }

        // Bilgi ikonu (Kullanıcı tıklanabileceğini anlasın diye)
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Detay",
            tint = Color.DarkGray
        )
    }
}