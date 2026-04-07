# MU Reev - Android Music Player 🎵

MU Reev adalah aplikasi pemutar musik Android modern yang dibangun dengan Kotlin. Aplikasi ini menawarkan pengalaman mendengarkan musik lokal yang kaya fitur dengan fokus pada personalisasi, statistik pendengaran, dan antarmuka pengguna yang bersih.

## ✨ Fitur Utama

*   **Pemutaran Musik Lokal**: Memindai dan memutar file audio (MP3, dll) langsung dari penyimpanan perangkat.
*   **Sistem Tema Dinamis**: Tersedia 5 pilihan tema warna (Cool Pink, Blue, Purple, Green, dan Black) yang dapat disesuaikan dengan selera pengguna.
*   **Statistik Pendengaran (Stats)**: Melacak lagu, artis, dan album yang paling sering diputar serta total waktu mendengarkan.
*   **Manajemen Playlist & Favorit**: Buat playlist kustom dan tandai lagu favorit Anda dengan mudah.
*   **Navigasi Berdasarkan Kategori**: Telusuri musik berdasarkan Lagu, Artis, atau Album.
*   **Pencarian Cepat**: Temukan lagu favorit Anda secara instan dengan fitur pencarian yang responsif.
*   **Integrasi Firebase**: Sistem login dan manajemen pengguna menggunakan Firebase Authentication.
*   **Lirik Lagu**: Dukungan untuk menampilkan lirik saat lagu diputar.
*   **Antarmuka Responsif**: Menggunakan Material Design Components dan View Binding untuk UI yang halus dan modern.

## 🛠️ Teknologi yang Digunakan

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: Android Jetpack (AppCompat, RecyclerView, ConstraintLayout)
*   **Architecture**: View Binding
*   **Database/Storage**: 
    *   SharedPreferences (untuk preferensi lokal & metadata)
    *   MediaStore API (untuk akses file media)
*   **Backend**: Firebase (Auth & Analytics)
*   **Libraries**:
    *   [Glide](https://github.com/bumptech/glide) - Loading gambar & album art.
    *   [Gson](https://github.com/google/gson) - Serialisasi data untuk playlist & favorit.
    *   [Jaudiotagger](http://www.jthink.net/jaudiotagger/) - Editing/reading metadata audio.
    *   [OkHttp](https://square.github.io/okhttp/) - Networking.

## 📸 Cuplikan Layar

*(Saran: Tambahkan beberapa screenshot aplikasi Anda di sini untuk menarik perhatian)*

| Main Screen | Player | Playlist | Stats |
| :---: | :---: | :---: | :---: |
| ![Main](https://via.placeholder.com/200x400) | ![Player](https://via.placeholder.com/200x400) | ![Playlist](https://via.placeholder.com/200x400) | ![Stats](https://via.placeholder.com/200x400) |

## 🚀 Cara Menjalankan

1.  **Clone Repositori**
    ```bash
    git clone https://github.com/username/MUReev.git
    ```
2.  **Buka di Android Studio**
    Pastikan Anda menggunakan versi Android Studio terbaru.
3.  **Konfigurasi Firebase**
    *   Buat proyek di [Firebase Console](https://console.firebase.google.com/).
    *   Tambahkan aplikasi Android dengan package name `com.example.mureev`.
    *   Unduh file `google-services.json` dan letakkan di folder `app/`.
    *   Aktifkan Authentication (Email/Password).
4.  **Build & Run**
    Hubungkan perangkat Android atau gunakan Emulator, lalu tekan tombol **Run**.

## 📋 Izin (Permissions)

Aplikasi ini memerlukan izin berikut untuk berfungsi dengan optimal:
*   `READ_EXTERNAL_STORAGE` (Android < 13)
*   `READ_MEDIA_AUDIO` (Android 13+)
*   `INTERNET` (Untuk Firebase & Lirik)

---
Developed with ❤️ by Arif
