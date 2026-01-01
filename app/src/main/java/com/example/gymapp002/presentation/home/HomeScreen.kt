package com.example.gymapp002.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.WorkoutEntity
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.util.FunWeightResult

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCreateWorkout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. HEADER
            HomeHeader(uiState.userName, uiState.streakDays)
            Spacer(modifier = Modifier.height(24.dp))

            // 2. BUGÜNÜN GÖREVİ (Gerçek Veri)
            TodayMissionCard(
                todaysWorkout = uiState.todaysWorkout, // ViewModel'den gelen veri
                onStartClick = {
                    // İLERİDE: Antrenmanı başlat
                    // onNavigateToActiveWorkout(uiState.todaysWorkout.workoutId)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. HIZLI ERİŞİM
            QuickActionsGrid(onCreateClick = onNavigateToCreateWorkout, onStatsClick = onNavigateToProfile)
            Spacer(modifier = Modifier.height(32.dp))

            // 4. HAFTALIK ÖZET (Şimdilik Statik, History Tablosu Gelince Canlanacak)
            Text("Haftalık Durum", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyProgressSection(uiState.weeklyCompleted, uiState.weeklyGoal)
            Spacer(modifier = Modifier.height(32.dp))

            // 5. SON AKTİVİTE (Fil/Tank - History Tablosu Gelince Canlanacak)
            if (uiState.funResult != null) {
                Text("Son Aktivite", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LastWorkoutFunCard(uiState.lastWorkoutName, uiState.lastWorkoutVolume, uiState.funResult!!)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 6. MOTİVASYON
            MotivationCard(quote = uiState.dailyQuote)
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- GÜNCELLENMİŞ BUGÜNÜN GÖREVİ KARTI ---
@Composable
fun TodayMissionCard(
    todaysWorkout: WorkoutEntity?, // Null olabilir
    onStartClick: () -> Unit
) {
    // Eğer bugün antrenman varsa: YEŞİL KART
    // Yoksa: GRİ DİNLENME KARTI

    val isRestDay = todaysWorkout == null

    val brush = if (isRestDay) {
        Brush.horizontalGradient(listOf(Color(0xFF424242), Color(0xFF212121))) // Koyu Gri
    } else {
        Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))) // Yeşil
    }

    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(140.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(brush)) {
            // Arka plan ikonu
            Icon(
                imageVector = if(isRestDay) Icons.Default.Bed else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(150.dp).align(Alignment.CenterEnd).offset(x = 30.dp)
            )

            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if(isRestDay) "BUGÜNÜN DURUMU" else "BUGÜNÜN HEDEFİ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todaysWorkout?.workoutName ?: "Dinlenme Günü",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = todaysWorkout?.duration ?: "İyi istirahatler ☕",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                if (!isRestDay) {
                    Button(
                        onClick = onStartClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("BAŞLAT", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// --- DİĞER BİLEŞENLER (Aynı) ---

@Composable
fun MotivationCard(quote: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.FormatQuote, null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).graphicsLayer { rotationZ = 180f })
        Spacer(modifier = Modifier.height(8.dp))
        Text(quote, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic, color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.width(40.dp).height(2.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)))
    }
}

@Composable
fun QuickActionsGrid(onCreateClick: () -> Unit, onStatsClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        QuickActionItem(Icons.Default.Add, "Yeni Plan", Color(0xFF4CAF50), Modifier.weight(1f), onCreateClick)
        QuickActionItem(Icons.Default.MonitorWeight, "Vücut", Color(0xFF2196F3), Modifier.weight(1f), onStatsClick)
        QuickActionItem(Icons.Default.History, "Geçmiş", Color(0xFFFF9800), Modifier.weight(1f), {})
        QuickActionItem(Icons.Default.Settings, "Ayarlar", Color.Gray, Modifier.weight(1f), {})
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).clickable { onClick() }, contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun HomeHeader(userName: String, streakDays: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Tekrar Hoşgeldin,", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(userName, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Surface(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(50), modifier = Modifier.height(40.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$streakDays Gün", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklyProgressSection(completed: Int, goal: Int) {
    val progress = if (goal > 0) completed.toFloat() / goal.toFloat() else 0f
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(50)).background(Color.DarkGray)) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primary))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text("$completed / $goal", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LastWorkoutFunCard(workoutName: String, totalVolume: Int, funData: FunWeightResult) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FitnessCenter, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Son: $workoutName ($totalVolume kg)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.DarkGray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                    Text(funData.icon, fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(funData.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(funData.message, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }
    }
}