package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.Exercise
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import com.example.gymapp002.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    onBackClick: () -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // ViewModel'den veriyi dinliyoruz
    val workoutDetail by viewModel.workoutDetails.collectAsState()

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
            // BAŞLAT BUTONU
            ExtendedFloatingActionButton(
                onClick = { /* İleride Antrenman Moduna Geçeceğiz */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                text = { Text("BAŞLAT", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->

        // Veri yüklenene kadar boş ekran veya loading dönebiliriz.
        // workoutDetail null değilse içeriği göster:
        workoutDetail?.let { detail ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                // 1. BAŞLIK VE BİLGİ KARTI
                WorkoutHeader(detail)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. EGZERSİZ LİSTESİ
                Text(
                    text = "Egzersizler (${detail.exercises.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp), // FAB altında kalmasın
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(detail.exercises) { exercise ->
                        // Hangi set/tekrar olduğunu bulmak için crossRef listesine bakmak gerekebilir
                        // Şimdilik basitçe egzersizi gösterelim
                        DetailExerciseCard(exercise)
                    }
                }
            }
        } ?: run {
            // Yükleniyor durumu
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = detail.workout.duration,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "•  ${detail.workout.difficulty}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DetailExerciseCard(exercise: Exercise) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
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

        Column {
            Text(exercise.name, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(exercise.muscleGroup, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}