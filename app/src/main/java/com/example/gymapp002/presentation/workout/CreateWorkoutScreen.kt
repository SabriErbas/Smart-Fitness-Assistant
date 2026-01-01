@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.data.local.entity.ExerciseEntity
import com.example.gymapp002.ui.AppViewModelProvider

@Composable
fun CreateWorkoutScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: CreateWorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- SEÇİCİ PENCERESİ ---
    if (uiState.isPickerVisible) {
        ExerciseSelectionDialog(
            exercises = uiState.filteredExercises,
            searchQuery = uiState.searchQuery,
            onSearchChange = { viewModel.updateSearchQuery(it) },
            onDismiss = { viewModel.togglePicker(false) },
            onExerciseSelected = { exercise -> viewModel.addExercise(exercise) }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yeni Plan Oluştur", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, null, tint = Color.White) }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveWorkout(onSuccess = onSaveClick) },
                        enabled = uiState.isSaveEnabled
                    ) {
                        Text("KAYDET", color = if (uiState.isSaveEnabled) MaterialTheme.colorScheme.primary else Color.Gray, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.togglePicker(true) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // 1. İSİM
            OutlinedTextField(
                value = uiState.workoutName,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Antrenman Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. SÜRE & MOLA
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WatchLater, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Tahmini Süre", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${uiState.calculatedDurationMin} dk", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, tint = Color(0xFFFFC107))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Set Arası", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${uiState.restTimeSeconds} sn", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.DarkGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // 3. PLANLAMA
            Text("Planlama Türü", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ScheduleTypeButton(
                    text = "Haftalık",
                    isSelected = uiState.scheduleType == "WEEKLY",
                    onClick = { viewModel.setScheduleType("WEEKLY") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                ScheduleTypeButton(
                    text = "Döngüsel",
                    isSelected = uiState.scheduleType == "CYCLIC",
                    onClick = { viewModel.setScheduleType("CYCLIC") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.scheduleType == "WEEKLY") {
                Text("Hangi Günler?", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val days = listOf("P", "S", "Ç", "P", "C", "C", "P")
                    days.forEachIndexed { index, label ->
                        val dayValue = index + 1
                        DayCircleButton(
                            label = label,
                            isSelected = uiState.selectedDays.contains(dayValue),
                            onClick = { viewModel.toggleDaySelection(dayValue) }
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Repeat, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kaç günde bir yapılsın?", color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${uiState.recurrenceGap} Gün", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.DarkGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // 4. LİSTE
            Text("Eklenecek Hareketler", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            if (uiState.selectedExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("+ Butonuna basarak hareket seç", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    itemsIndexed(uiState.selectedExercises) { index, item ->
                        ReorderableExerciseCard(
                            item = item,
                            isFirst = index == 0,
                            isLast = index == uiState.selectedExercises.size - 1,
                            onMoveUp = { viewModel.moveExercise(index, -1) },
                            onMoveDown = { viewModel.moveExercise(index, 1) },
                            onRemove = { viewModel.removeExercise(item) },
                            // YENİ: Set/Tekrar değiştiğinde ViewModel'e haber ver
                            onDetailsChange = { newSets, newReps ->
                                viewModel.updateExerciseDetails(index, newSets, newReps)
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- DÜZELTİLMİŞ KART BİLEŞENİ (Set/Tekrar Düzenlenebilir) ---

@Composable
fun ReorderableExerciseCard(
    item: SelectedExerciseItem,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onDetailsChange: (String, String) -> Unit // YENİ PARAMETRE
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. YUKARI / AŞAĞI BUTONLARI
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Yukarı",
                        tint = if (!isFirst) Color.White else Color.DarkGray.copy(alpha = 0.3f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Aşağı",
                        tint = if (!isLast) Color.White else Color.DarkGray.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. İSİM VE GİRİŞ ALANLARI (ORTA)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Set ve Tekrar Giriş Kutuları (Yan Yana)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // SET SAYISI
                    OutlinedTextField(
                        value = item.sets,
                        onValueChange = { onDetailsChange(it, item.reps) },
                        label = { Text("Set", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    // TEKRAR SAYISI
                    OutlinedTextField(
                        value = item.reps,
                        onValueChange = { onDetailsChange(item.sets, it) },
                        label = { Text("Tekrar", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. SİL BUTONU
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Sil", tint = Color.Gray)
            }
        }
    }
}

// --- DİĞER YARDIMCI BİLEŞENLER ---

@Composable
fun DayCircleButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScheduleTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray,
            contentColor = if (isSelected) Color.Black else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

// --- SEÇİM PENCERESİ (Aynı kaldı) ---
@Composable
fun ExerciseSelectionDialog(
    exercises: List<ExerciseEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseEntity) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Başlık
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hareket Seç", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Arama
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Ara...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Liste
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exercises) { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A2A2A))
                                .clickable { onExerciseSelected(exercise) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exercise.name.take(1),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(exercise.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(exercise.muscleGroup, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}