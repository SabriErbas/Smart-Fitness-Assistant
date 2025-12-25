package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp002.ui.AppViewModelProvider
import com.example.gymapp002.data.local.entity.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: CreateWorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val workoutName by viewModel.workoutName.collectAsState()
    val scheduleType by viewModel.scheduleType.collectAsState()
    val cycleGap by viewModel.cycleGap.collectAsState()

    // Bottom Sheet (Egzersiz Seçici) Durumu
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CreateWorkoutTopBar(
                onBackClick = onBackClick,
                canSave = workoutName.isNotBlank() && viewModel.selectedExercises.isNotEmpty(),
                onSave = { viewModel.saveWorkout(onSuccess = onSaveClick) }
            )
        },
        floatingActionButton = {
            // Egzersiz Ekleme Butonu (FAB)
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary, // Asit Yeşili
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. ANTRENMAN İSMİ
            OutlinedTextField(
                value = workoutName,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Antrenman Adı", color = Color.Gray) },
                placeholder = { Text("Örn: Göğüs & Biceps", color = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. PLANLAMA (Schedule) BÖLÜMÜ
            Text(
                text = "Program Sıklığı",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Toggle (Haftalık vs Döngüsel)
            ScheduleTypeSelector(
                currentType = scheduleType,
                onTypeSelected = { viewModel.setScheduleType(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seçime göre değişen içerik
            if (scheduleType == ScheduleType.WEEKLY) {
                // Pzt, Sal, Çar...
                WeeklyDaySelector(
                    selectedDays = viewModel.selectedDays,
                    onDayToggle = { viewModel.toggleDaySelection(it) }
                )
            } else {
                // Her X Günde Bir
                CyclicInputSelector(
                    gap = cycleGap,
                    onGapChange = { viewModel.onCycleGapChange(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SEÇİLEN EGZERSİZLER LİSTESİ
            Text(
                text = "Egzersizler (${viewModel.selectedExercises.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // FAB altında kalmasın
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.selectedExercises) { item ->
                    AddedExerciseCard(
                        item = item,
                        onUpdate = { sets, reps -> viewModel.updateSetsReps(item.exercise.exerciseId, sets, reps) },
                        onRemove = { viewModel.removeExercise(item.exercise.exerciseId) }
                    )
                }
            }
        }

        // 4. BOTTOM SHEET (Egzersiz Seçimi İçin)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface // Koyu gri zemin
            ) {
                ExercisePickerSheetContent(
                    viewModel = viewModel,
                    onClose = { showBottomSheet = false }
                )
            }
        }
    }
}

// --- ALT BİLEŞENLER ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutTopBar(onBackClick: () -> Unit, canSave: Boolean, onSave: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Yeni Plan", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            TextButton(onClick = onSave, enabled = canSave) {
                Text(
                    text = "KAYDET",
                    color = if (canSave) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ScheduleTypeSelector(currentType: ScheduleType, onTypeSelected: (ScheduleType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray)
            .padding(4.dp)
    ) {
        // Haftalık Butonu
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (currentType == ScheduleType.WEEKLY) MaterialTheme.colorScheme.surface else Color.Transparent)
                .clickable { onTypeSelected(ScheduleType.WEEKLY) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Haftalık",
                color = if (currentType == ScheduleType.WEEKLY) MaterialTheme.colorScheme.primary else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }

        // Döngüsel Butonu
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (currentType == ScheduleType.CYCLIC) MaterialTheme.colorScheme.surface else Color.Transparent)
                .clickable { onTypeSelected(ScheduleType.CYCLIC) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Döngüsel",
                color = if (currentType == ScheduleType.CYCLIC) MaterialTheme.colorScheme.primary else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WeeklyDaySelector(selectedDays: List<Int>, onDayToggle: (Int) -> Unit) {
    val days = listOf("P", "S", "Ç", "P", "C", "C", "P")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, dayName ->
            val dayIndex = index + 1
            val isSelected = selectedDays.contains(dayIndex)

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .border(1.dp, if (isSelected) Color.Transparent else Color.Gray, CircleShape)
                    .clickable { onDayToggle(dayIndex) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CyclicInputSelector(gap: String, onGapChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text("Her", color = Color.White)
        Spacer(modifier = Modifier.width(12.dp))

        OutlinedTextField(
            value = gap,
            onValueChange = onGapChange,
            modifier = Modifier.width(60.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(12.dp))
        Text("günde bir tekrarla", color = Color.White)
    }
}

@Composable
fun AddedExerciseCard(
    item: SelectedExerciseState,
    onUpdate: (String, String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İsim ve Kas Grubu
            Column(modifier = Modifier.weight(1f)) {
                Text(item.exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(item.exercise.muscleGroup, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }

            // Set Input
            CompactInput(value = item.sets, label = "Set", onValueChange = { onUpdate(it, item.reps) })
            Spacer(modifier = Modifier.width(8.dp))
            // Reps Input
            CompactInput(value = item.reps, label = "Tekrar", onValueChange = { onUpdate(item.sets, it) })

            Spacer(modifier = Modifier.width(8.dp))

            // Sil Butonu
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun CompactInput(value: String, label: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.width(50.dp).height(45.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun ExercisePickerSheetContent(viewModel: CreateWorkoutViewModel, onClose: () -> Unit) {
    val allExercises by viewModel.allExercises.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp) // Sheet yüksekliği
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Egzersiz Seç", style = MaterialTheme.typography.titleLarge, color = Color.White)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(allExercises) { exercise ->
                val isSelected = viewModel.selectedExercises.any { it.exercise.exerciseId == exercise.exerciseId }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.toggleExerciseSelection(exercise) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(exercise.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(exercise.muscleGroup, color = Color.Gray, fontSize = 12.sp)
                    }
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Divider(color = Color.DarkGray, thickness = 0.5.dp)
            }
        }
    }
}