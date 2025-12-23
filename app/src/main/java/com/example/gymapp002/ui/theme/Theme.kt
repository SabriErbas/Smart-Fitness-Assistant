package com.example.gymapp002.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Senin Tasarımına Uygun Renk Şeması
val MainColorScheme = darkColorScheme(
    primary = AcidLime,            // Butonlar, aktif bar çubukları
    onPrimary = Color.Black,       // Yeşil buton üzerindeki yazı rengi (Okunabilirlik için siyah)
    primaryContainer = AcidLime,
    onPrimaryContainer = Color.Black,

    secondary = MutedGold,         // Logo ve ikincil vurgular
    onSecondary = Color.Black,

    background = BlackBackground,  // Tüm sayfa arka planı
    onBackground = TextWhite,      // Arka plan üzerindeki yazılar

    surface = DarkSurface,         // Kartlar, input alanları
    onSurface = TextWhite,         // Kart üzerindeki yazılar
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextGray,   // Input label'ları gibi silik yazılar

    error = ErrorRed
)

@Composable
fun GymApp002Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // DİKKAT: Dynamic color'ı 'false' yaptık.
    // True kalırsa Android 12+ cihazlarda duvar kağıdı rengini alır, senin yeşil tasarımın bozulur.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Bu tasarım karanlık (Dark) odaklı olduğu için Light modda bile Dark şemayı kullanabilirsin
        // veya LightColorScheme tanımlayıp onu da verebilirsin.
        // Şimdilik tutarlılık için her durumda senin temanı veriyorum:
        else -> MainColorScheme
    }

    // Durum Çubuğu (Status Bar) Rengi Ayarı
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar'ı arka planla aynı renk yapıyoruz
            window.statusBarColor = MainColorScheme.background.toArgb()
            // İkonları beyaz yap (Dark mode olduğu için)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Typography.kt dosyan varsa oradan alır
        content = content
    )
}