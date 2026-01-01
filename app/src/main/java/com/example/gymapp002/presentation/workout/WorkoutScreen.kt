package com.example.gymapp002.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.ui.components.WeekCalendar
import com.example.gymapp002.data.local.entity.WorkoutWithExercises
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen(
    onNavigateToCreateWorkout: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("PLAN", "Antrenman") // İsmi 'Antrenman'dan 'Kütüphane'ye çevirdim, daha mantıklı

    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyPlan by viewModel.dailyPlan.collectAsState()
    val libraryWorkouts by viewModel.libraryWorkouts.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CustomTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            when (selectedTabIndex) {
                0 -> UserPlanSection(
                    selectedDate = selectedDate,
                    dailyPlan = dailyPlan,
                    onDateSelected = { viewModel.onDateSelected(it) },
                    onCreateClick = onNavigateToCreateWorkout,
                    onWorkoutClick = onNavigateToDetail
                )
                1 -> GeneralWorkoutList(
                    workouts = libraryWorkouts,
                    onWorkoutClick = onNavigateToDetail,
                    onDeleteClick = { workoutId -> viewModel.deleteWorkout(workoutId) }
                )
            }
        }
    }
}

// --- PLAN SEKMESİ (Aynı kaldı) ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserPlanSection(
    selectedDate: LocalDate,
    dailyPlan: List<WorkoutWithExercises>,
    onDateSelected: (LocalDate) -> Unit,
    onCreateClick: () -> Unit,
    onWorkoutClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        WeekCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color.DarkGray, thickness = 0.5.dp)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        val dateString = selectedDate.format(formatter)

        Text(
            text = "$dateString Planı",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        if (dailyPlan.isEmpty()) {
            EmptyPlanState(onCreateClick)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dailyPlan) { item ->
                    PlannedWorkoutCard(
                        item = item,
                        onClick = { onWorkoutClick(item.workout.workoutId) }
                    )
                }
                item {
                    Button(
                        onClick = onCreateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Başka Antrenman Ekle", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPlanState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bugün için plan yok.", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "+ Antrenman Ekle")
        }
    }
}

@Composable
fun PlannedWorkoutCard(
    item: WorkoutWithExercises,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = item.workout.workoutName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.exercises.size} Hareket • ${item.workout.duration}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            item.exercises.take(3).forEach { exercise ->
                Text(
                    text = "• ${exercise.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun CustomTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary,
                height = 3.dp
            )
        },
        divider = {
            Divider(color = Color.DarkGray, thickness = 0.5.dp)
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTabIndex == index)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray
                    )
                }
            )
        }
    }
}

// --- KÜTÜPHANE LİSTESİ (SİSTEM MANTIĞI EKLENDİ) ---

@Composable
fun GeneralWorkoutList(
    workouts: List<WorkoutWithExercises>,
    onWorkoutClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    if (workouts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Henüz hiç antrenman yok.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workouts) { item ->
                LibraryWorkoutCard(
                    item = item,
                    onClick = { onWorkoutClick(item.workout.workoutId) },
                    onDeleteClick = { onDeleteClick(item.workout.workoutId) }
                )
            }
        }
    }
}

@Composable
fun LibraryWorkoutCard(
    item: WorkoutWithExercises,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Sistem antrenmanı mı kontrolü
    val isSystem = item.workout.isSystemWorkout

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isSystem) Color(0xFF252525) else MaterialTheme.colorScheme.surface
        ),
        // Sistem antrenmanlarına hafif bir çerçeve (Border) ekleyelim ki özel olduğu belli olsun
        border = if (isSystem) androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.workout.workoutName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if(isSystem) Color(0xFFBB86FC) else Color.White // Sistem ise morumsu
                        )
                        if (isSystem) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFBB86FC), modifier = Modifier.size(16.dp))
                        }
                    }

                    Text(
                        text = "${item.exercises.size} Egzersiz • ${item.workout.difficulty}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // SİLME MANTIĞI BURADA
                if (isSystem) {
                    // Sistem antrenmanıysa KİLİT ikonu göster, silme yok
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "System Workout",
                        tint = Color.DarkGray
                    )
                } else {
                    // Kullanıcı antrenmanıysa SİLME butonu göster
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Antrenmanı Sil",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            item.exercises.take(2).forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
            if (item.exercises.size > 2) {
                Text("...", color = Color.Gray)
            }
        }
    }
}