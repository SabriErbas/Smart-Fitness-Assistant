package com.example.gymapp002.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Bugünden itibaren sonraki 14 günü listele
    val days = remember {
        (0..14).map { LocalDate.now().plusDays(it.toLong()) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Başlık: Seçilen Ay ve Yıl (Örn: December 2025)
        Text(
            text = "${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.year}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(days) { date ->
                CalendarDayItem(
                    date = date,
                    isSelected = date == selectedDate,
                    onDateClick = { onDateSelected(date) }
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateClick: () -> Unit
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) // Pzt, Sal...
    val dayNumber = date.dayOfMonth.toString() // 25, 26...

    // Kart Tasarımı
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(85.dp)
            .clickable { onDateClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Seçiliyse YEŞİL (Acid Lime), değilse KOYU GRİ (Surface)
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Gün Adı (Pzt)
            Text(
                text = dayName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.Black else Color.Gray, // Seçiliyse Siyah yazı
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gün Numarası (25)
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleLarge,
                color = if (isSelected) Color.Black else Color.White, // Seçiliyse Siyah yazı
                fontWeight = FontWeight.Bold
            )

            // Bugün İşareti (Opsiyonel: Eğer tarih 'bugün' ise altına minik nokta koyabiliriz)
            if (date == LocalDate.now() && !isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}