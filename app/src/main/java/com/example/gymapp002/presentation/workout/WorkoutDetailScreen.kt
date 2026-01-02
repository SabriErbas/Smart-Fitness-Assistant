package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
import com.example.gymapp002.ui.AppViewModelProvider

@Composable
fun WorkoutDetailScreen(
    onBackClick: () -> Unit,
    onStartWorkout: (Int) -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val info = uiState.workoutInfo

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (info != null) {
                Button(
                    onClick = { onStartWorkout(info.workout.workoutId) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ANTRENMANI BAŞLAT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (info != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. HEADER
                item {
                    DetailHeader(
                        workoutName = info.workout.workoutName,
                        duration = info.workout.duration,
                        difficulty = info.workout.difficulty,
                        exerciseCount = uiState.exercises.size,
                        onBackClick = onBackClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Hareket Listesi",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. SIRALI VE DÜZENLENEBİLİR LİSTE
                itemsIndexed(uiState.exercises) { index, item ->
                    ReorderableDetailItem(
                        item = item,
                        index = index,
                        isFirst = index == 0,
                        isLast = index == uiState.exercises.size - 1,
                        onMoveUp = { viewModel.moveExercise(index, -1) },
                        onMoveDown = { viewModel.moveExercise(index, 1) }
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

// --- YENİ BİLEŞEN: OK TUŞLARI OLAN DETAY KARTI ---
@Composable
fun ReorderableDetailItem(
    item: WorkoutDetailItem,
    index: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // YUKARI / AŞAĞI BUTONLARI (SOLDA)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = onMoveUp,
                enabled = !isFirst,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Yukarı",
                    tint = if (!isFirst) Color.Gray else Color.DarkGray.copy(alpha = 0.3f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            IconButton(
                onClick = onMoveDown,
                enabled = !isLast,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Aşağı",
                    tint = if (!isLast) Color.Gray else Color.DarkGray.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // SIRA NUMARASI
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // BİLGİLER (ORTA)
        Column(modifier = Modifier.weight(1f)) {
            Text(item.exercise.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(item.exercise.muscleGroup, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }

        // SET x TEKRAR (SAĞ - GERÇEK VERİ)
        Column(horizontalAlignment = Alignment.End) {
            Text("${item.sets} Set", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("${item.reps} Tekrar", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// (DetailHeader ve DetailStatChip fonksiyonları aynı kalacak, onları tekrar yazmadım.
// Önceki koddakini koruyabilirsin.)
@Composable
fun DetailHeader(
    workoutName: String,
    duration: String,
    difficulty: String,
    exerciseCount: Int,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2A2A), MaterialTheme.colorScheme.background)
                )
            )
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = workoutName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailStatChip(icon = Icons.Default.AccessTime, text = duration)
                DetailStatChip(icon = Icons.Default.SignalCellularAlt, text = difficulty)
                DetailStatChip(icon = Icons.Default.FitnessCenter, text = "$exerciseCount Hareket")
            }
        }
    }
}

@Composable
fun DetailStatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}