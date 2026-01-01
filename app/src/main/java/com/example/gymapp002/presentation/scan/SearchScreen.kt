package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.data.local.entity.ExerciseEntity as Exercise

// Veritabanı tablosunu import ediyoruz


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // ViewModel'den gelen CANLI verileri dinliyoruz
    val searchQuery by viewModel.searchQuery.collectAsState()
    val exerciseList by viewModel.exerciseList.collectAsState()

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

            // 2. ARAMA ÇUBUĞU
            SearchBarWithCamera(
                query = searchQuery,
                onQueryChange = { newText ->
                    // Kritik Nokta: Her harf yazıldığında ViewModel'e haber veriyoruz
                    viewModel.onSearchQueryChanged(newText)
                },
                onCameraClick = { /* İleride Makine Tanıma sistemini buraya bağlayacağız */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. EGZERSİZ LİSTESİ (Veritabanından Gelen)
            Text(
                text = "Tüm Egzersizler (${exerciseList.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Mock liste yerine artık gerçek 'exerciseList' kullanıyoruz
                items(exerciseList) { exercise ->
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
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "AI Camera Scan",
                    tint = MaterialTheme.colorScheme.primary // ASİT YEŞİLİ
                )
            }
        }
    )
}

@Composable
fun ExerciseRowCard(exercise: Exercise) { // Artık Exercise alıyor
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Egzersiz detayına git */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // DarkSurface
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
                    text = exercise.name, // Entity'den gelen isim
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exercise.muscleGroup, // Entity'den gelen kas grubu
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary // Kas grubu Yeşil olsun
                    )
                    Text(
                        text = " • ${exercise.difficulty}", // Entity'den gelen zorluk
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Sağ İkon
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Detail",
                tint = Color.Gray
            )
        }
    }
}