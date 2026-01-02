package com.example.gymapp002.util

// Dönüşüm sonucu bize bu veri paketiyle gelecek
data class FunWeightResult(
    val title: String,    // Örn: "Yetişkin Bir Ayı"
    val icon: String,     // Örn: "🐻"
    val message: String   // Örn: "Ormanın kralı olmaya az kaldı!"
)

object FunWeightConverter {

    // Eşik değerleri (kg cinsinden) ve karşılıkları
    // Sıralama Küçükten -> Büyüğe doğru olmalı
    private val milestones = mapOf(
        0 to FunWeightResult("Tüy Sıklet", "🪶", "Isınma turları bitti, şimdi başlama zamanı!"),
        50 to FunWeightResult("Dolu Pazar Arabası", "🛒", "Haftalık alışverişi tek seferde taşıdın!"),
        100 to FunWeightResult("Yavru Panda", "🐼", "Çok tatlı ama hafife alma!"),
        200 to FunWeightResult("Motorsiklet", "🏍️", "Motoru sırtlayıp götürecek güçtesin."),
        350 to FunWeightResult("Yetişkin Bir Aslan", "🦁", "Ormanın kralıyla güreşebilecek seviyedesin."),
        500 to FunWeightResult("Kuyruklu Piyano", "🎹", "Sanat ve güç bir arada!"),
        850 to FunWeightResult("Tofaş Şahin", "🚗", "Sanayiye gitmene gerek yok, sırtla gitsin!"),
        1500 to FunWeightResult("Hipopotam", "🦛", "Suyun kaldırma kuvvetine ihtiyacın yok."),
        3000 to FunWeightResult("Yetişkin Bir Fil", "🐘", "Hortumuyla sana saygı duruşunda bulunuyor!"),
        5000 to FunWeightResult("Okul Otobüsü", "🚌", "Tüm sınıfı sırtında taşıyorsun resmen."),
        12000 to FunWeightResult("F-16 Savaş Uçağı", "✈️", "Yer çekimine meydan okuyorsun!"),
        40000 to FunWeightResult("T-62 Tankı", "🛡️", "Bu artık spor değil, savaş hazırlığı!"),
        100000 to FunWeightResult("Uzay Mekiği", "🚀", "NASA seni göreve çağırıyor!")
    )

    /**
     * Toplam ağırlığı (Volume) alır, en uygun nesneye çevirir.
     * Örn: 900 kg kaldırdıysa, 850 kg (Tofaş) sonucunu döner.
     */
    fun convert(totalVolumeKg: Double): FunWeightResult {
        // Verilen kilodan küçük veya eşit olan en büyük eşik değerini bul
        // (Floor Entry mantığı)
        val match = milestones.filterKeys { it <= totalVolumeKg }
            .maxByOrNull { it.key }

        return match?.value ?: milestones[0]!! // Hiçbiri uymazsa Tüy Sıklet döner
    }
}