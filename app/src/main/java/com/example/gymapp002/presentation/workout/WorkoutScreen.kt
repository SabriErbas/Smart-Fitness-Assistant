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
// Oluşturduğumuz Takvim bileşenini import ediyoruz
import com.example.gymapp002.ui.components.WeekCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- MOCK DATA (Veri Modelleri) ---
data class Exercise(
    val name: String,
    val detail: String,
    val sets: String
)

data class WorkoutProgram(
    val title: String,
    val subtitle: String,
    val exercises: List<Exercise>
)

@RequiresApi(Build.VERSION_CODES.O) // LocalDate kullanımı için gerekli
@Composable
fun WorkoutScreen() {
    // Sekme yönetimi
    var selectedTabIndex by remember { mutableIntStateOf(0) } // Başlangıçta 0 (PLAN) açık olsun
    val tabs = listOf("PLAN", "ANTRENMAN")

    // Tarih Yönetimi (Takvim için)
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // BlackBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. ÜST SEKME (TABS)
            CustomTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            // 2. İÇERİK ALANI
            when (selectedTabIndex) {
                0 -> UserPlanSection(
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> selectedDate = newDate }
                )
                1 -> GeneralWorkoutList()  // Sağ Sekme: Genel Antrenmanlar
            }
        }
    }
}

// --- BİLEŞENLER (COMPONENTS) ---

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserPlanSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- 1. TAKVİM BİLEŞENİ (En Tepede) ---
        WeekCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color.DarkGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. GÜNLÜK PLAN İÇERİĞİ ---
        // Burada seçilen tarihe göre veritabanından veri çekeceğiz.
        // Şimdilik boş durum (Empty State) gösteriyoruz.

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Kalan alanı doldur
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tarihi güzel formatla gösterelim (Örn: 25 December)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
            val dateString = selectedDate.format(formatter)

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$dateString Planı",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bu tarih için planlanmış bir antrenman yok.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "Antrenman Ekle" Butonu (Kullanıcıyı teşvik etmek için)
            Button(
                onClick = { /* CreateWorkoutScreen'e git */ },
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
}

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
    // Mock Data
    val programs = listOf(
        WorkoutProgram(
            title = "Tüm vücut antrenmanı",
            subtitle = "7 egzersiz",
            exercises = listOf(
                Exercise("Çiftçi Taşıması", "Dambıl", "4x00:30"),
                Exercise("Ölüm kaldırışı", "Halter barı", "4x8"),
                Exercise("Göğüs Presi", "Halter barı", "4x8")
            )
        ),
        WorkoutProgram(
            title = "Göğüs antrenmanı",
            subtitle = "5 egzersiz",
            exercises = listOf(
                Exercise("Aşağı Eğimli Göğüs Presi", "Dambıl", "4x8"),
                Exercise("Göğüs Presi", "Halter barı", "4x8"),
                Exercise("Ayakta Göğüs Açma", "Kablo", "4x8")
            )
        ),
        WorkoutProgram(
            title = "Sırt Antrenmanı",
            subtitle = "5 egzersiz",
            exercises = listOf(
                Exercise("Eğilerek Çekme", "Halter barı", "4x8"),
                Exercise("Sırt Yan Kasları", "Kablo", "4x8"),
                Exercise("Ölüm kaldırışı", "Halter barı", "4x8")
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
fun WorkoutCard(program: WorkoutProgram) {
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
                        imageVector = Icons.Default.DateRange,
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tümünü göster",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Detay sayfasına git */ }
            )
        }
    }
}