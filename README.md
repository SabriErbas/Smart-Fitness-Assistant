# 🏋️ Akıllı Fitness Antrenörü

> **AI destekli görüntü işleme + IoT sensör füzyonu ile çalışan, cihaz üzerinde (on-device) gerçek zamanlı fitness asistanı.**

---

## 📌 Proje Hakkında

**Akıllı Fitness Antrenörü**, kullanıcının telefon kamerasını bir fitness aletine doğrultmasıyla o aleti milisaniyeler içinde tanıyan, kullanım yönergelerini ekrana getiren ve harici IoT sensörleri aracılığıyla hareket formunu anlık olarak analiz eden bir Android uygulamasıdır.

### Çözdüğü Problemler

- 🤔 **"Bunu nasıl kullanıyorum?"** — Karmaşık makinelerin kullanımını bilmeme ve spor salonunda yanlış yapma kaygısı
- 🤕 **Sakatlanma riski** — Hatalı form veya aletin yanlış kullanımından kaynaklanan yaralanmalar
- ⏱️ **Tempo kaybı** — Hangi aletin hangi kası çalıştırdığını araştırmak için harcanan vakit

---

## 🧠 Mimari & Teknoloji Yığını

### 📱 Mobil Uygulama Katmanı

| Bileşen | Teknoloji | Neden? |
|---|---|---|
| Platform | **Native Android + Kotlin** | Donanım hakimiyeti ve düşük gecikme |
| UI Framework | **Jetpack Compose** | Saniyede defalarca güncellenen AI/sensör verisi için deklaratif state yönetimi |
| Mimari Desen | **MVVM** | İş mantığı ile UI'yı birbirinden izole eder |
| Asenkron İşlemler | **Kotlin Coroutines & Flow** | Bluetooth dinleme ve kamera akışı için ana thread'i boşaltır |
| Yerel Veritabanı | **Room Database** | Antrenman geçmişi ve kullanıcı verisi |

---

### 🤖 AI & Görüntü İşleme Katmanı

```
Kamera Döngüsü → CameraX → Frame Hazırlama → TFLite Modeli → Sınıf Skoru → UI
```

| Bileşen | Teknoloji | Detay |
|---|---|---|
| Kamera API | **CameraX** | ~30 FPS, AI'ya uygun frame hazırlama |
| AI Motor | **TensorFlow Lite (.tflite)** | Sunucusuz, tamamen cihaz üzerinde çalışır (Edge Computing) |
| Optimizasyon | **Quantization** | Model sıkıştırılarak mobil işlemci yükü minimize edildi |
| Girdi Formatı | RGB → 224×224 px → ByteBuffer | Standart MobileNet pipeline |

**Tanınan Fitness Aletleri:**
- Preacher Curl
- Leg Extension
- Lat Pull Down
- T-Row

> ⚠️ **Not:** Model şu an 4 pozitif sınıf içermektedir. `Other/Background` negatif sınıfı bir sonraki fazda eklenerek false positive oranı düşürülecektir.

---

### ⚙️ IoT & Oyunlaştırma Katmanı

```
Sensörler → ESP32-S3 (On-Chip Filtreleme) → BLE 5.0 → Android App → Gerçek Zamanlı UI
```

| Bileşen | Teknoloji | Görev |
|---|---|---|
| Mikrodenetleyici | **ESP32-S3** (çift çekirdek) | Dahili BLE 5.0, yüksek işlem gücü |
| Mesafe Ölçümü | **Ultrasonik Sensör** | Hareket genliği ve tekrar sayımı |
| Sarsıntı/Açı | **Jiroskop** | Form bozukluğu tespiti |
| İletişim | **Bluetooth Low Energy (BLE)** | Düşük gecikmeli, pil dostu veri aktarımı |

---

## 🎮 Sistemin Çalışma Akışı

### Akış 1 — AI Makine Tanıma

```
1. CameraX → ~30 FPS yakalanır
2. Frame → RGB dönüşüm → 224x224 px yeniden ölçekleme → ByteBuffer
3. TFLite modeli → 4 sınıf için [0.0 – 1.0] olasılık üretir
4. Güven eşiği: Threshold > %80 (ardışık karelerde tutarlı)
5. Eşik aşılırsa → UI güncellenir, eğitim videosu açılır
```

### Akış 2 — IoT Oyunlaştırma

```
1. Kalibrasyon: İlk tekrar ultrasonik sensörle ölçülür → [0.0 – 1.0] normalize
2. On-Chip Processing: ESP32, ham sensör verisini filtreler → tek anlamlı BLE paketi
3. Hedef Takibi: Uygulamada "İdeal Referans Dalgası" (sinüs formu) akar
4. Kullanıcı, kendi hareket çizgisini bu ideal dalganın üstüne oturtmaya çalışır
```

---

## 🚀 Kurulum

### Gereksinimler

- Android Studio (Jetpack Compose uyumlu)
- JDK 17
- KVM donanım hızlandırması
- **Fiziksel Android cihaz** (BLE testleri için zorunlu)
- ESP32-S3 geliştirme kartı + Ultrasonik sensör + Jiroskop kalkanı

### Android Uygulamasını Derleme

```bash
# Repoyu klonla
git clone https://github.com/kullanici-adi/akilli-fitness-antrenoru.git
cd akilli-fitness-antrenoru

# Debug APK derle
./gradlew clean assembleDebug
```

### ESP32-S3 Donanım Kurulumu

Mikrodenetleyiciye yazılmış C/C++ firmware aşağıdaki görevleri üstlenir:

1. Ultrasonik sensör ve jiroskoptan ham veri okuma
2. Gürültü filtreleme (Noise Filtering)
3. İşlenmiş veriyi BLE GATT Notify karakteristiği üzerinden yayınlama

Firmware'i ESP32-S3'e flaşlamak için [ESP-IDF](https://docs.espressif.com/projects/esp-idf/en/latest/) veya Arduino IDE kullanılabilir.

---

## 🗺️ Yol Haritası

### ✅ Tamamlanan
- [x] TFLite ile 4 aletin cihaz üzerinde tanınması
- [x] ESP32-S3 + BLE ile anlık sensör veri akışı
- [x] İdeal referans dalgasıyla hareket karşılaştırma (oyunlaştırma)
- [ ] Daha fazla fitness aleti için model genişletme

### 🤖 AI & Görüntü İşleme

- [ ] **Negatif Sınıf (Background Class) Entegrasyonu** — Veri setine ilgisiz nesneler ve boş salon görüntülerinden oluşan güçlü bir `Other/Background` sınıfı eklenip model yeniden eğitilecek. Sistemin şu an her nesneyi 4 aletten birine benzetmesinin (False Positive) önüne geçilecek.
- [ ] **On-Device Pose Estimation** — MediaPipe veya MoveNet gibi hafif edge modelleriyle kullanıcının iskelet yapısı (skeleton tracking) kamera akışı üzerinden gerçek zamanlı analiz edilecek. Omuz açısı, bel bükülmesi gibi form bozuklukları doğrudan görsel veriden yakalanabilecek.
- [ ] **Edge NLP ile Çevrimdışı Sesli Komut** — Antrenman esnasında terli ellerle telefona dokunma sorununu ortadan kaldırmak için TFLite tabanlı küçük NLP modelleri entegre edilecek. *"Seti bitirdim"*, *"Sonraki hareket ne?"* gibi komutlar internet gerektirmeden, sıfır gecikmeyle işlenecek.

### ⚙️ IoT & Donanım

- [ ] **Gelişmiş Sensör Füzyonu (Kalman Filtresi)** — Ham jiroskop ve ivmeölçer verisi basit eşik değerleri yerine matematiksel Kalman Filtresi algoritmasıyla işlenecek. Sensör gürültüsü neredeyse sıfıra indirilerek oyun modülündeki referans dalgası çok daha pürüzsüz akacak.
- [ ] **Giyilebilir Cihaz (Wearable) Entegrasyonu** — Özel donanım üretme maliyeti ve arıza riskini bertaraf etmek için sensör verisi toplama işi kullanıcının akıllı saatine (Wear OS / Apple Watch) kaydırılacak; BLE üzerinden saatin dahili IMU sensörleri okunacak.

### 📊 Uygulama & UX

- [x] Antrenman geçmişi & ilerleme grafikleri

---

## 🛠️ Teknik Notlar

> **On-Chip Processing Kararı:** Ham sensör verisi Bluetooth bandına yığılmak yerine ESP32 üzerinde filtrelenmektedir. Bu tercih, uygulama arayüzündeki olası gecikmeleri (lag) kökeninden önler.

> **False Positive Riski:** Mevcut 4 sınıflı model, tanımadığı nesneleri de bu 4 aletten birine zorla eşleştirebilir. Bir sonraki fazda negatif sınıf eklenmesi planlanmaktadır.

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) altında dağıtılmaktadır.
