# 🏋️ Smart Fitness Trainer

> **A real-time, on-device fitness assistant powered by AI-driven computer vision and IoT sensor fusion.**

---

## 📌 About the Project

**Smart Fitness Trainer** is an Android application that recognizes gym equipment within milliseconds by pointing the phone camera at it, instantly displays usage instructions, and analyzes the user's movement form in real time through external IoT sensors.

### Problems It Solves

- 🤔 **"How do I use this?"** — Not knowing how to operate complex machines and the anxiety of doing it wrong at the gym
- 🤕 **Injury risk** — Injuries caused by poor form or incorrect use of equipment
- ⏱️ **Wasted time** — Time spent figuring out which machine targets which muscle group

---

## 🧠 Architecture & Tech Stack

### 📱 Mobile Application Layer

| Component | Technology | Why? |
|---|---|---|
| Platform | **Native Android + Kotlin** | Full hardware control and minimal latency |
| UI Framework | **Jetpack Compose** | Declarative state management for AI/sensor data updating dozens of times per second |
| Architecture Pattern | **MVVM** | Isolates business logic from the UI layer |
| Async Operations | **Kotlin Coroutines & Flow** | Offloads Bluetooth listening and camera stream from the main thread |
| Local Database | **Room Database** | Workout history and user data persistence |

---

### 🤖 AI & Computer Vision Layer

```
Camera Loop → CameraX → Frame Preparation → TFLite Model → Class Score → UI
```

| Component | Technology | Detail |
|---|---|---|
| Camera API | **CameraX** | ~30 FPS capture, frame preparation for AI inference |
| AI Engine | **TensorFlow Lite (.tflite)** | Fully on-device inference, no server required (Edge Computing) |
| Optimization | **Quantization** | Model compressed to minimize mobile CPU load |
| Input Format | RGB → 224×224 px → ByteBuffer | Standard MobileNet pipeline |

**Recognized Gym Equipment:**
- Preacher Curl
- Leg Extension
- Lat Pull Down
- T-Row

> ⚠️ **Note:** The current model contains 4 positive classes only. An `Other/Background` negative class will be added in the next phase to reduce false positive detections.

---

### ⚙️ IoT & Gamification Layer

```
Sensors → ESP32-S3 (On-Chip Filtering) → BLE 5.0 → Android App → Real-Time UI
```

| Component | Technology | Role |
|---|---|---|
| Microcontroller | **ESP32-S3** (dual-core) | Built-in BLE 5.0, high processing power |
| Distance Measurement | **Ultrasonic Sensor** | Range of motion and rep counting |
| Vibration / Angle | **Gyroscope** | Form deviation detection |
| Communication | **Bluetooth Low Energy (BLE)** | Low-latency, battery-friendly data transfer |

---

## 🎮 System Workflow

### Pipeline 1 — AI Equipment Recognition

```
1. CameraX captures ~30 FPS
2. Frame → RGB conversion → 224×224 px rescaling → ByteBuffer
3. TFLite model produces probability scores [0.0 – 1.0] for each of the 4 classes
4. Confidence threshold: > 80% sustained across consecutive frames
5. Threshold met → UI updates and training video is launched
```

### Pipeline 2 — IoT Gamification

```
1. Calibration: First rep is measured by the ultrasonic sensor → normalized to [0.0 – 1.0]
2. On-Chip Processing: ESP32 filters hundreds of raw sensor readings → sends one clean BLE packet
3. Target Tracking: An "Ideal Reference Wave" (sine curve) built from professional trainer data flows on screen
4. User tries to align their live movement curve on top of the ideal wave
```

---

## 🚀 Getting Started

### Requirements

- Android Studio (Jetpack Compose compatible)
- JDK 17
- KVM hardware acceleration
- **Physical Android device** (required for BLE testing)
- ESP32-S3 development board + Ultrasonic sensor + Gyroscope shield

### Building the Android App

```bash
# Clone the repository
git clone https://github.com/SabriErbas/Smart-Fitness-Trainer.git
cd smart-fitness-trainer

# Build debug APK
./gradlew clean assembleDebug
```

### ESP32-S3 Hardware Setup

The C/C++ firmware flashed onto the microcontroller handles:

1. Reading raw data from the ultrasonic sensor and gyroscope
2. Noise filtering on-chip
3. Broadcasting the processed data via BLE GATT Notify characteristic

Use [ESP-IDF](https://docs.espressif.com/projects/esp-idf/en/latest/) or Arduino IDE to flash the firmware onto the ESP32-S3.

---

## 🗺️ Roadmap

### ✅ Completed
- [x] On-device recognition of 4 gym machines via TFLite
- [x] Real-time sensor data streaming with ESP32-S3 + BLE
- [x] Movement comparison against ideal reference wave (gamification)

### 🤖 AI & Computer Vision

- [ ] **Background Class Integration** — A robust `Other/Background` negative class will be added to the dataset using irrelevant objects and empty gym footage, then the model will be retrained to eliminate false positive detections.
- [ ] **On-Device Pose Estimation** — Lightweight edge models such as MediaPipe or MoveNet will be integrated for real-time skeleton tracking via the camera stream, enabling direct detection of form errors like shoulder angle deviation or back rounding.
- [ ] **Offline Voice Commands via Edge NLP** — TFLite-based compact NLP models will be embedded to eliminate the need for touchscreen interaction during workouts. Commands like *"Set complete"* or *"What's the next exercise?"* will be processed on-device with zero latency and no internet connection.
- [ ] Expand model to support more gym equipment

### ⚙️ IoT & Hardware

- [ ] **Advanced Sensor Fusion (Kalman Filter)** — Raw gyroscope and accelerometer data will be processed through a Kalman Filter algorithm instead of simple threshold comparisons, reducing sensor noise to near zero and making the gamification reference wave significantly smoother.
- [ ] **Wearable Device Integration** — To eliminate custom hardware production costs and failure risks, sensor data collection will be offloaded to the user's smartwatch (Wear OS / Apple Watch), reading the watch's built-in IMU sensors over BLE.

### 📊 App & UX

- [ ] Workout history & progress charts

---

## 🛠️ Technical Notes

> **On-Chip Processing Decision:** Raw sensor data is filtered directly on the ESP32 rather than being streamed raw over Bluetooth. This design choice eliminates potential UI lag at its source.

> **False Positive Risk:** The current 4-class model may forcefully map unrecognized objects to one of the 4 equipment classes. Adding a negative class in the next phase is planned to address this.

---

## 📄 License

This project is distributed under the [Apache 2.0 License](LICENSE).
