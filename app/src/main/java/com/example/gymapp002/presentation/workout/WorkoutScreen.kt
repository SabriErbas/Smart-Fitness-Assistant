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

// --- MOCK DATA (Genel Liste İçin Gerekli Sınıflar) ---
data class MockExercise(
    val name: String,
    val detail: String,
    val sets: String
)

data class MockWorkoutProgram(
    val title: String,
    val subtitle: String,
    val exercises: List<MockExercise>
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen(
    onNavigateToCreateWorkout: () -> Unit,
    onNavigateToDetail: (Int) -> Unit, // ID ile detay sayfasına gitme görevi
    viewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("PLAN", "ANTRENMAN")

    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyPlan by viewModel.dailyPlan.collectAsState()

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
                    onWorkoutClick = onNavigateToDetail // Tıklamayı aşağı aktarıyoruz
                )
                1 -> GeneralWorkoutList()
            }
        }
    }
}

// --- PLAN SEKMESİ BİLEŞENLERİ ---

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserPlanSection(
    selectedDate: LocalDate,
    dailyPlan: List<WorkoutWithExercises>,
    onDateSelected: (LocalDate) -> Unit,
    onCreateClick: () -> Unit,
    onWorkoutClick: (Int) -> Unit // Kart tıklandığında çalışacak fonksiyon
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
                    // GÜNCELLEME BURADA YAPILDI: ID GÖNDERİLİYOR
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
    onClick: () -> Unit // GÜNCELLEME: Tıklama parametresi eklendi
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // GÜNCELLEME: Modifier.clickable eklendi
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

// --- DİĞER BİLEŞENLER ---

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

@Composable
fun GeneralWorkoutList() {
    val programs = listOf(
        MockWorkoutProgram(
            title = "Tüm vücut antrenmanı",
            subtitle = "7 egzersiz",
            exercises = listOf(
                MockExercise("Çiftçi Taşıması", "Dambıl", "4x00:30"),
                MockExercise("Ölüm kaldırışı", "Halter barı", "4x8")
            )
        ),
        MockWorkoutProgram(
            title = "Göğüs antrenmanı",
            subtitle = "5 egzersiz",
            exercises = listOf(
                MockExercise("Bench Press", "Halter", "4x8"),
                MockExercise("Fly", "Kablo", "4x12")
            )
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(programs) { program ->
            WorkoutCard(program)
        }
    }
}

@Composable
fun WorkoutCard(program: MockWorkoutProgram) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    Text(
                        text = program.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = program.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange, // Düzeltildi
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            program.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${exercise.name} · ${exercise.detail}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = exercise.sets,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}