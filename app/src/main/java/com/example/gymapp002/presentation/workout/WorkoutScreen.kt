package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- MOCK DATA (Veri Modelleri) ---
// Gerçek projede bunları ayrı bir 'model' paketine taşıyabilirsin.
data class Exercise(
    val name: String,
    val detail: String, // "Dambıl", "Halter barı" vb.
    val sets: String    // "4x8" veya "4x00:30"
)

data class WorkoutProgram(
    val title: String,
    val subtitle: String,
    val exercises: List<Exercise>
)

@Composable
fun WorkoutScreen() {
    // Sekme yönetimi için state
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Başlangıçta 1 (ANTRENMAN) açık olsun
    val tabs = listOf("PLAN", "ANTRENMAN")

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
            // Seçilen sekmeye göre içeriği değiştiriyoruz
            when (selectedTabIndex) {
                0 -> UserPlanSection()     // Sol Sekme: Kullanıcının Planı
                1 -> GeneralWorkoutList()  // Sağ Sekme: Genel Antrenmanlar
            }
        }
    }
}

// --- BİLEŞENLER (COMPONENTS) ---

@Composable
fun CustomTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary, // Acid Lime
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary, // Çizgi Rengi: Yeşil
                height = 3.dp
            )
        },
        divider = {
            Divider(color = Color.DarkGray, thickness = 0.5.dp) // Alt çizgi
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
                            MaterialTheme.colorScheme.primary // Seçiliyse Yeşil
                        else
                            Color.Gray // Değilse Gri
                    )
                }
            )
        }
    }
}

@Composable
fun GeneralWorkoutList() {
    // Referans resimdeki verileri simüle ediyoruz
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
        ),
        WorkoutProgram(
            title = "Kol egzersizi",
            subtitle = "6 egzersiz",
            exercises = listOf(
                Exercise("Pazı Bükme", "Halter barı", "4x8"),
                Exercise("Üç Başlı Kas", "Kablo", "4x8"),
                Exercise("Ters Bükme", "Halter barı", "4x8")
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
fun UserPlanSection() {
    // BURASI "PLAN" SEKMESİ (Sol Taraf)
    // Şimdilik boş bir placeholder koyuyoruz, istersen burayı "Bugünün Antrenmanı" gibi doldurabiliriz.
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Size Atanan Plan",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Text(
            text = "Henüz aktif bir antrenman programınız yok.",
            color = Color.Gray
        )
    }
}

@Composable
fun WorkoutCard(program: WorkoutProgram) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // DarkSurface (#232A2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // --- HEADER ---
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

                // Kas Grubu Resmi (Placeholder)
                // Gerçek resim için Image() kullanabilirsin. Şimdilik gri daire.
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary // Yeşil İkon
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- EGZERSİZ LİSTESİ ---
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

            // --- FOOTER (Tümünü Göster) ---
            Text(
                text = "Tümünü göster",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary, // Acid Lime Rengi
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Detay sayfasına git */ }
            )
        }
    }
}