package com.example.gymapp002.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

// --- OYUN AYARLARI ---
const val WAVE_SPEED = 7f
const val WAVE_FREQUENCY = 0.006f
const val PATH_THICKNESS = 200f
const val BALL_RADIUS = 45f
const val SMOOTHING_FACTOR = 0.08f

@Composable
fun GameOverlay(
    exerciseName: String,
    onDismiss: () -> Unit,
    // GÜNCELLEME: Artık hem Skoru hem de Tekrar Sayısını döndürüyoruz
    onGameFinished: (score: Int, reps: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // --- SİMÜLASYON SLIDER (0f - 1f) ---
    var simSensorValue by remember { mutableFloatStateOf(0.5f) }

    // --- OYUN STATE'LERİ ---
    var time by remember { mutableFloatStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }

    // YENİ: Tekrar Sayma Mantığı İçin State'ler
    var repCount by remember { mutableIntStateOf(0) }
    var isRepTopReached by remember { mutableStateOf(false) } // Tepeye ulaştı mı?

    var countdown by remember { mutableIntStateOf(5) }
    var isGameRunning by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("HAZIRLAN!") }
    var statusColor by remember { mutableStateOf(Color(0xFF00E5FF)) }
    var smoothedPercent by remember { mutableFloatStateOf(50f) }

    // --- GERİ SAYIM ---
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        isGameRunning = true
        feedbackText = "BAŞLA!"
    }

    // --- OYUN DÖNGÜSÜ ---
    LaunchedEffect(Unit) {
        while (true) {
            time += WAVE_SPEED
            delay(16)
        }
    }

    // --- FİZİK VE TEKRAR HESAPLAMA ---
    // Hareket Yumuşatma
    val targetPercent = simSensorValue * 100
    smoothedPercent += (targetPercent - smoothedPercent) * SMOOTHING_FACTOR

    // 🚀 YENİ: TEKRAR SAYMA ALGORİTMASI (Schmitt Trigger)
    // 1. Eğer sensör %80'in üzerine çıkarsa -> "Tepeye Ulaştık" (Hareketi kaldırdık)
    if (simSensorValue > 0.8f && !isRepTopReached) {
        isRepTopReached = true
    }
    // 2. Eğer tepeye ulaşmışsak ve %20'nin altına inersek -> "Tekrar Bitti" (Hareketi indirdik)
    if (simSensorValue < 0.2f && isRepTopReached) {
        if (isGameRunning) { // Sadece oyun başladıysa say
            repCount++
        }
        isRepTopReached = false // Sıfırla
    }

    // --- ARAYÜZ ---
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // YAN ÇEVRİLMİŞ EKRAN KUTUSU
            Box(
                modifier = Modifier
                    .size(width = screenHeight, height = screenWidth)
                    .rotate(90f)
                    .background(Color(0xFF101010))
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val canvasW = maxWidth.value
                    val canvasH = maxHeight.value

                    // Fizik Değişkenleri
                    val centerScreenY = canvasH * 0.5f
                    val waveAmplitude = canvasH * 0.35f
                    val normalizedInput = (smoothedPercent - 50) / 50f
                    val playerY = centerScreenY - (normalizedInput * waveAmplitude)
                    val playerX = canvasW * 0.2f

                    // Hedef Kontrolü
                    val currentWaveY = centerScreenY + (waveAmplitude * sin((playerX + time) * WAVE_FREQUENCY))
                    val diff = playerY - currentWaveY.toFloat()
                    val limit = PATH_THICKNESS / 2

                    if (isGameRunning) {
                        if (abs(diff) < limit) {
                            statusColor = Color(0xFF00E676)
                            feedbackText = "HARİKA!"
                            if (time.toInt() % 10 == 0) score += 1
                        } else {
                            if (diff < -limit) {
                                statusColor = Color(0xFFFFEA00)
                                feedbackText = "AŞAĞI İN"
                            } else {
                                statusColor = Color(0xFFFF3D00)
                                feedbackText = "YUKARI ÇIK"
                            }
                        }
                    } else {
                        statusColor = Color.Gray
                    }

                    // --- ÇİZİM ---
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Tünel Çizimi (Aynı kalıyor, yer tasarrufu için kısalttım)
                        val path = Path()
                        for (x in -100..size.width.toInt() + 100 step 20) {
                            val y = centerScreenY + (waveAmplitude * sin((x + time) * WAVE_FREQUENCY))
                            if (x == -100) path.moveTo(x.toFloat(), y.toFloat()) else path.lineTo(x.toFloat(), y.toFloat())
                        }
                        drawPath(path = path, color = statusColor.copy(alpha = if (isGameRunning) 0.25f else 0.1f), style = Stroke(width = PATH_THICKNESS))

                        // Oyuncu Topu
                        drawCircle(color = if(isGameRunning) Color.White else Color.Gray, radius = BALL_RADIUS, center = Offset(playerX, playerY))
                    }

                    // --- UI KATMANLARI ---

                    // Geri Sayım
                    if (!isGameRunning) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)), contentAlignment = Alignment.Center) {
                            Text("$countdown", color = Color.White, fontSize = 120.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // BİLGİ PANELİ
                    if (isGameRunning) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 20.dp)
                                .fillMaxWidth(0.8f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // SOL: Puan
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PUAN", color = Color.Gray, fontSize = 12.sp)
                                Text("$score", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }

                            // ORTA: Mesaj
                            Text(feedbackText, color = statusColor, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))

                            // SAĞ: Tekrar Sayısı (YENİ)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TEKRAR", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                Text("$repCount", color = MaterialTheme.colorScheme.primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // ÇIKIŞ
                    IconButton(
                        onClick = {
                            onGameFinished(score, repCount) // VERİYİ GERİ GÖNDER
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(24.dp).background(Color.White.copy(0.1f), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.Close, "Kapat", tint = Color.White)
                    }

                    // SLIDER
                    Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp).fillMaxWidth(0.6f)) {
                        Slider(value = simSensorValue, onValueChange = { simSensorValue = it }, valueRange = 0f..1f, colors = SliderDefaults.colors(thumbColor = statusColor, activeTrackColor = statusColor))
                    }
                }
            }
        }
    }
}