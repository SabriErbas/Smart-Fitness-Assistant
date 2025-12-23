package com.example.gymapp002.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Eğer R.drawable.profile_pic hata verirse, kendi resim dosyanı ekleyene kadar bu satırı yorum satırına al
// import com.example.gymapp002.R

@Composable
fun ProfileScreen() {
    // Scaffold arka plan rengini temadan alır (BlackBackground)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp), // Yanlardan boşluk
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. ÜST KISIM: Profil Resmi ve İsim
            ProfileHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // 2. İSTATİSTİK KARTLARI (Boy, Kilo, Yaş)
            StatsSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 3. ACCOUNT MENÜSÜ
            SectionTitle(title = "Account")
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(icon = Icons.Outlined.Person, text = "Personal Data")
            ProfileMenuItem(icon = Icons.Outlined.Star, text = "Achievement") // İkonu uygun olanla değiştir
            ProfileMenuItem(icon = Icons.Outlined.DateRange, text = "Activity History")
            ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, text = "Like") // "Like" için kalp ikonu

            Spacer(modifier = Modifier.height(24.dp))

            // 4. NOTIFICATION MENÜSÜ
            SectionTitle(title = "Notification")
            Spacer(modifier = Modifier.height(8.dp))

            NotificationToggleItem()
        }
    }
}

// --- ALT BİLEŞENLER (COMPONENTS) ---

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profil Resmi
        // Not: 'R.drawable.user_placeholder' yerine projenin res/drawable klasörüne
        // bir resim atıp onu kullanabilirsin. Şimdilik gri bir daire koyuyorum.
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            // Gerçek resmi buraya şöyle ekleyeceksin:
            /*
            Image(
                painter = painterResource(id = R.drawable.profile_pic),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            */
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // İsim ve Email
        Column {
            Text(
                text = "Jefro Suirop",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Jefro SuiropKu@gmail.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Gri ton
            )
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Kartlar arası boşluk
    ) {
        // Weight(1f) diyerek her karta eşit genişlik veriyoruz
        StatCard(value = "180cm", label = "Height", modifier = Modifier.weight(1f))
        StatCard(value = "80kg", label = "Weight", modifier = Modifier.weight(1f))
        StatCard(value = "22yo", label = "Age", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface, // Theme.kt'deki DarkSurface
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary, // Acid Lime Rengi
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        // İnce çizgi (Divider)
        Divider(color = Color.DarkGray, thickness = 0.5.dp)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { /* Tıklama işlemi */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, // İkonlar Yeşil mi olsun Beyaz mı? Resimde beyaz/gri duruyor.
            // Eğer resimdeki gibi gri istiyorsan burayı Color.Gray veya Color.White yapabilirsin.
            // Ben tema uyumu için Primary (Yeşil) yaptım ama White da şık durur:
            // tint = Color.White
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color.Gray
        )
    }
}

@Composable
fun NotificationToggleItem() {
    var isChecked by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = Color.White // Resimde bildirim ikonu beyaz
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Pop-Up notification",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5E5CE6), // Resimdeki Mavi/Mor renk
                // Eğer kendi temanı (Asit Yeşili) kullanmak istersen:
                // checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}