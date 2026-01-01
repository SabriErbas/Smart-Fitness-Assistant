package com.example.gymapp002.util

object MotivationProvider {

    private val quotes = listOf(
        "Disiplin, ne istediğini asla unutmama sanatıdır.",
        "Kod çalışmıyorsa debug yaparsın. Kas gelişmiyorsa ağırlığı artırırsın.",
        "Yarınki sen, bugün başladığın için sana teşekkür edecek.",
        "Acı geçicidir, ama pes etmenin pişmanlığı sonsuza dek sürer.",
        "Vücudun, zihninin ona emrettiği her şeye dayanabilir.",
        "Bahane üretme. Sonuç üret.",
        "Bir saatlik antrenman, gününün sadece %4'üdür. Bahanen yok.",
        "Motivasyon seni başlatır, alışkanlık devam ettirir.",
        "Zor, 'imkansız' demek değildir. Sadece 'uğraşman gerek' demektir.",
        "Sırtını dik tut, omuzlarını geriye al. Dünya seni izliyor.",
        "Baştan aksak başlayan her iş, aksayarak devam etmeye mahkumdur."
    )

    fun getRandomQuote(): String {
        return quotes.random()
    }
}