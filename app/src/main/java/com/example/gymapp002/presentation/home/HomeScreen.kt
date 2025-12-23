package com.example.gymapp002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // BlackBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState) // Ekran kaydırılabilir olsun
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. ÜST BAŞLIK (Merhaba Jefro)
            HomeHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. HAFTALIK İLERLEME BARI (Resimdeki stile sadık kalarak)
            WeeklyProgressSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 3. GÜNÜN ANTRENMANI (Today's Workout)
            SectionTitle(title = "Bugünün Hedefi")
            Spacer(modifier = Modifier.height(16.dp))
            TodayWorkoutCard(
                muscleGroup = "Göğüs & Arka Kol",
                duration = "45 Dk",
                exerciseCount = 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. BENİM BONUSUM: AI MACHINE SCANNER (Senin projen için)
            // Kullanıcı makineyi tanımak için hızlıca buraya basabilir.
            AIScannerCard()

            Spacer(modifier = Modifier.height(32.dp))

            // 5. MOTİVASYON SÖZÜ
            DailyQuoteSection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- BİLEŞENLER (COMPONENTS) ---

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hoş geldin,",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = "Jefro Suirop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Bildirim İkonu
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface) // DarkSurface
                .clickable { /* Bildirimlere git */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color.White
            )
            // Kırmızı nokta (Yeni bildirim var efekti)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error) // Kırmızı
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = 10.dp)
            )
        }
    }
}

@Composable
fun WeeklyProgressSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Haftalık Aktivite",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Tamamlanan: 3/5",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary // Asit Yeşili
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barların olduğu satır
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // Grafik yüksekliği
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom // Barlar aşağıdan yukarı büyüsün
        ) {
            // Örnek Veri: Günler ve Doluluk oranları (0.0f - 1.0f)
            val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
            val progress = listOf(0.8f, 0.4f, 1.0f, 0.2f, 0.0f, 0.6f, 0.0f)

            days.forEachIndexed { index, day ->
                ActivityBar(
                    day = day,
                    progress = progress[index],
                    isToday = index == 2 // Örnek: Çarşamba bugün olsun
                )
            }
        }
    }
}

@Composable
fun ActivityBar(day: String, progress: Float, isToday: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.End
    ) {
        // Barın kendisi
        Box(
            modifier = Modifier
                .width(32.dp) // Bar kalınlığı
                .weight(1f) // Parent yüksekliğini doldurması için
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surface), // Arka plan (Boş kısım)
            contentAlignment = Alignment.BottomCenter
        ) {
            // Dolu kısım
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(progress) // Doluluk oranı
                    .background(
                        if (progress > 0) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gün etiketi
        Text(
            text = day,
            style = MaterialTheme.typography.labelMedium,
            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TodayWorkoutCard(muscleGroup: String, duration: String, exerciseCount: Int) {
    // Büyük, dikkat çekici kart
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arka plana hafif bir görsel veya gradient koyabilirsin
            // Şimdilik sağ tarafa dekoratif bir daire koyalım
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 100.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst Kısım: Başlık ve Rozetler
                Column {
                    Text(
                        text = muscleGroup,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Badge(text = duration)
                        Badge(text = "$exerciseCount Hareket")
                    }
                }

                // Alt Kısım: "Başla" Butonu
                Button(
                    onClick = { /* Antrenmana başla */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Asit Yeşili
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Antrenmana Başla", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AIScannerCard() {
    // Senin projenin özel özelliği: Makine Tanıma
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Kamerayı aç */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C3E50) // Biraz farklı bir ton veya Gradient olabilir
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Makine Tanıma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Bu makine ne işe yarar? Kameranı aç ve öğren.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            // Kamera İkonu Kutusu
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Camera",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun DailyQuoteSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "“Başarı, her gün tekrarlanan küçük çabaların toplamıdır.”",
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "- Robert Collier",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary // Yazar ismi yeşil olsun
        )
    }
}

@Composable
fun Badge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}