package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.* // remember, mutableStateOf burada
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.WorkoutDetailItem
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.ui.game.GameOverlay // <-- YENİ OYUN IMPORTU

@Composable
fun ActiveWorkoutScreen(
    onFinishClick: () -> Unit,
    viewModel: ActiveWorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- OYUN STATE'LERİ (YENİ EKLENDİ) ---
    var showGame by remember { mutableStateOf(false) }
    var activeGameExerciseName by remember { mutableStateOf("") }
    // -------------------------------------

    val minutes = uiState.timerSeconds / 60
    val seconds = uiState.timerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    // --- OYUN KATMANI (Eğer butona basıldıysa açılır) ---
    if (showGame) {
        GameOverlay(
            exerciseName = activeGameExerciseName,
            onDismiss = { showGame = false },
            onScoreUpdate = { score ->
                // İleride buraya skor kaydetme mantığı ekleyebiliriz
                println("Oyun Bitti! Skor: $score")
                showGame = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // ANTRENMANI BİTİR BUTONU
            Button(
                onClick = {
                    viewModel.finishWorkout {
                        onFinishClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Stop, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ANTRENMANI BİTİR", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. HEADER
            TimerHeader(
                title = uiState.workoutTitle,
                time = timeFormatted,
                isPaused = uiState.isPaused,
                onPauseToggle = { viewModel.togglePause() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. LİSTE
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.exerciseOrder) { exerciseDetail ->
                        val sets = uiState.setList[exerciseDetail.exercise.exerciseId] ?: emptyList()

                        ExerciseLoggingCard(
                            detailItem = exerciseDetail,
                            sets = sets,
                            onSetUpdate = { index, w, r ->
                                viewModel.updateSetData(exerciseDetail.exercise.exerciseId, index, w, r)
                            },
                            onSetComplete = { index ->
                                viewModel.toggleSetComplete(exerciseDetail.exercise.exerciseId, index)
                            },
                            onGameClick = {
                                // --- OYUNU BAŞLATMA LOGİĞİ (GÜNCELLENDİ) ---
                                activeGameExerciseName = exerciseDetail.exercise.name
                                showGame = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// ... (TimerHeader, ExerciseLoggingCard ve CompactInput fonksiyonları AYNEN kalabilir, aşağıda tekrar veriyorum tam olsun diye) ...

@Composable
fun TimerHeader(title: String, time: String, isPaused: Boolean, onPauseToggle: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFF252525), RoundedCornerShape(50))
                .padding(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(time, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onPauseToggle,
                modifier = Modifier.background(Color.White.copy(0.1f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = if(isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ExerciseLoggingCard(
    detailItem: WorkoutDetailItem,
    sets: List<ExerciseSetItem>,
    onSetUpdate: (Int, String, String) -> Unit,
    onSetComplete: (Int) -> Unit,
    onGameClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık ve Oyun Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(detailItem.exercise.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${sets.size} Set Planlandı", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                // EĞER OYUN VARSA BUTONU GÖSTER
                if (detailItem.exercise.isGameEnabled) {
                    Button(
                        onClick = onGameClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OYNA", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tablo Başlıkları
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("SET", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.weight(1f))
                Text("KG", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(16.dp))
                Text("TEKRAR", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(16.dp))
                Text("✓", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Set Satırları
            sets.forEachIndexed { index, setItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            if(setItem.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Set Numarası
                    Text(
                        "${setItem.setNumber}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Kilo Girişi
                    CompactInput(
                        value = setItem.weight,
                        onValueChange = { onSetUpdate(index, it, setItem.reps) },
                        placeholder = "-",
                        modifier = Modifier.width(60.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Tekrar Girişi
                    CompactInput(
                        value = setItem.reps,
                        onValueChange = { onSetUpdate(index, setItem.weight, it) },
                        placeholder = "0",
                        modifier = Modifier.width(60.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Tik Butonu
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (setItem.isCompleted) Color(0xFF4CAF50) else Color.DarkGray)
                            .clickable { onSetComplete(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompactInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier
            .height(36.dp)
            .background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
            .wrapContentHeight(Alignment.CenterVertically)
    )
}