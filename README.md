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

*   **Main Screen**: <img width="1220" height="2712" alt="image" src="https://github.com/user-attachments/assets/9fa83a7c-9b12-43bc-970e-83178fd99ffa" />
*   **Player**: <img width="1220" height="2712" alt="image" src="https://github.com/user-attachments/assets/515dfe0f-6e56-4e75-ac67-21932055d6a6" />
*   **Playlist**: <img width="1220" height="2712" alt="image" src="https://github.com/user-attachments/assets/f2dfedad-c856-4640-bae0-7359f5df153b" />
*   **Stats**:  <img width="1220" height="2712" alt="image" src="https://github.com/user-attachments/assets/627c1986-9a2d-4dc2-84d4-d056ca38bf87" />

