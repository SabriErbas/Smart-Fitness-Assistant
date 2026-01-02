package com.example.gymapp002.ui.game

import androidx.compose.animation.core.animateFloatAsState
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
    onScoreUpdate: (Int) -> Unit
) {
    // --- CİHAZ BOYUTLARI (MANÜPÜLASYON İÇİN) ---
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // --- SİMÜLASYON SLIDER (0f - 1f) ---
    var simSensorValue by remember { mutableFloatStateOf(0.5f) }

    // --- OYUN STATE'LERİ ---
    var time by remember { mutableFloatStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }

    // Geri Sayım State'leri
    var countdown by remember { mutableIntStateOf(5) }
    var isGameRunning by remember { mutableStateOf(false) } // 5 sn bitince true olur

    var feedbackText by remember { mutableStateOf("HAZIRLAN!") }
    var statusColor by remember { mutableStateOf(Color(0xFF00E5FF)) }

    // Hareket Yumuşatma
    var smoothedPercent by remember { mutableFloatStateOf(50f) }

    // --- GERİ SAYIM LOJİĞİ ---
    LaunchedEffect(Unit) {
        // 5 saniye say
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        // Süre bitti, oyunu başlat
        isGameRunning = true
        feedbackText = "BAŞLA!"
    }

    // --- OYUN FİZİK DÖNGÜSÜ ---
    LaunchedEffect(Unit) {
        while (true) {
            // Oyun arkada hep aksın ama puan sadece oyun başlayınca sayılsın
            time += WAVE_SPEED
            delay(16)
        }
    }

    // Hareket Yumuşatma (Lerp)
    val targetPercent = simSensorValue * 100
    smoothedPercent += (targetPercent - smoothedPercent) * SMOOTHING_FACTOR

    // --- TAM EKRAN DIALOG ---
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        // Siyah Arka Plan (Tüm Ekranı Kaplar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // --- MANÜPÜLASYON KUTUSU ---
            // Buradaki Box'ı 90 derece çeviriyoruz ve boyutlarını değiştiriyoruz.
            // Genişlik = Ekran Yüksekliği, Yükseklik = Ekran Genişliği
            Box(
                modifier = Modifier
                    .size(width = screenHeight, height = screenWidth) // BOYUTLARI TERS ÇEVİR
                    .rotate(90f) // YAN ÇEVİR
                    .background(Color(0xFF101010))
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val canvasW = maxWidth.value
                    val canvasH = maxHeight.value

                    // --- FİZİK HESAPLAMALARI ---
                    val centerScreenY = canvasH * 0.5f // Tam orta (Yatay modda olduğumuz için artık ortalayabiliriz)
                    val waveAmplitude = canvasH * 0.35f

                    // Oyuncu Pozisyonu
                    val normalizedInput = (smoothedPercent - 50) / 50f
                    // NOT: Slider sağa çekilince (değer artınca) top YUKARI gitsin istiyoruz.
                    // Canvas Y aşağı arttığı için çıkarma işlemi yapıyoruz.
                    val playerY = centerScreenY - (normalizedInput * waveAmplitude)
                    val playerX = canvasW * 0.2f // Top biraz solda dursun

                    // Hedef Kontrolü
                    val currentWaveY = centerScreenY + (waveAmplitude * sin((playerX + time) * WAVE_FREQUENCY))
                    val diff = playerY - currentWaveY.toFloat()
                    val limit = PATH_THICKNESS / 2

                    // Sadece oyun başladıysa geri bildirim ver
                    if (isGameRunning) {
                        if (abs(diff) < limit) {
                            statusColor = Color(0xFF00E676) // Yeşil
                            feedbackText = "HARİKA!"
                            if (time.toInt() % 10 == 0) score += 1
                        } else {
                            if (diff < -limit) {
                                statusColor = Color(0xFFFFEA00) // Sarı
                                feedbackText = "AŞAĞI İN"
                            } else {
                                statusColor = Color(0xFFFF3D00) // Kırmızı
                                feedbackText = "YUKARI ÇIK"
                            }
                        }
                    } else {
                        statusColor = Color.Gray
                    }

                    // --- ÇİZİM ---
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // A. SİNÜS TÜNELİ
                        val path = Path()
                        for (x in -100..size.width.toInt() + 100 step 20) {
                            val y = centerScreenY + (waveAmplitude * sin((x + time) * WAVE_FREQUENCY))
                            if (x == -100) path.moveTo(x.toFloat(), y.toFloat())
                            else path.lineTo(x.toFloat(), y.toFloat())
                        }

                        // Tünel Gövdesi
                        drawPath(
                            path = path,
                            color = statusColor.copy(alpha = if (isGameRunning) 0.25f else 0.1f),
                            style = Stroke(width = PATH_THICKNESS)
                        )

                        // Sınır Çizgileri
                        val borderPathTop = Path()
                        val borderPathBottom = Path()
                        for (x in -100..size.width.toInt() + 100 step 20) {
                            val waveY = centerScreenY + (waveAmplitude * sin((x + time) * WAVE_FREQUENCY))
                            val topY = waveY - (PATH_THICKNESS / 2)
                            val bottomY = waveY + (PATH_THICKNESS / 2)

                            if (x == -100) {
                                borderPathTop.moveTo(x.toFloat(), topY.toFloat())
                                borderPathBottom.moveTo(x.toFloat(), bottomY.toFloat())
                            } else {
                                borderPathTop.lineTo(x.toFloat(), topY.toFloat())
                                borderPathBottom.lineTo(x.toFloat(), bottomY.toFloat())
                            }
                        }
                        drawPath(path = borderPathTop, color = statusColor.copy(alpha = 0.8f), style = Stroke(width = 5f))
                        drawPath(path = borderPathBottom, color = statusColor.copy(alpha = 0.8f), style = Stroke(width = 5f))

                        // B. OYUNCU TOPU
                        drawCircle(
                            color = if(isGameRunning) Color.White else Color.Gray,
                            radius = BALL_RADIUS,
                            center = Offset(playerX, playerY)
                        )
                    }

                    // --- ARAYÜZ KATMANLARI ---

                    // 1. GERİ SAYIM EKRANI (OVERLAY)
                    if (!isGameRunning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)), // Hafif karartma
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$countdown",
                                    color = Color.White,
                                    fontSize = 120.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "HAZIRLAN",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 2. OYUN BİLGİLERİ (Üst Kısım - Yatayda Üst)
                    if (isGameRunning) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = exerciseName.uppercase(),
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$score",
                                color = Color.White,
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = feedbackText,
                                color = statusColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 3. ÇIKIŞ BUTONU (Sağ Üst)
                    IconButton(
                        onClick = {
                            onScoreUpdate(score)
                            onDismiss()
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(24.dp)
                            .background(Color.White.copy(0.1f), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }

                    // 4. SİMÜLASYON SLIDER (Test İçin - Alta Sabitlendi)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 30.dp)
                            .fillMaxWidth(0.6f) // Slider çok geniş olmasın
                    ) {
                        Slider(
                            value = simSensorValue,
                            onValueChange = { simSensorValue = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = statusColor,
                                activeTrackColor = statusColor
                            )
                        )
                    }
                }
            }
        }
    }
}