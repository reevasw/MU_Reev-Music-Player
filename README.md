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

| Main Screen | Player | Playlist | Stats |
| :---: | :---: | :---: | :---: |
| ![Main]([https://via.placeholder.com/200x400](https://media.discordapp.net/attachments/1461727943389610114/1488854899545477140/Screenshot_2026-04-01-17-54-21-861_com.example.mureev.jpg?ex=69ce4bad&is=69ccfa2d&hm=aec5f8b9f29d8d59a4029063ea3f9a3a14ab85db007436c46c796add5680574b&=&format=webp&width=385&height=856)) | 
  ![Player]([https://via.placeholder.com/200x400](https://media.discordapp.net/attachments/1461727943389610114/1488854899872501894/Screenshot_2026-04-01-17-54-32-003_com.example.mureev.jpg?ex=69ce4bad&is=69ccfa2d&hm=22cd09978f37832b5489abe0bff33e9a489ef6e7b076f250e3c86bd357c20176&=&format=webp&width=385&height=856)) | 
  ![Playlist]([https://via.placeholder.com/200x400](https://media.discordapp.net/attachments/1461727943389610114/1488854900204114001/Screenshot_2026-04-01-17-56-49-029_com.example.mureev.jpg?ex=69ce4bad&is=69ccfa2d&hm=1daf0d280cda410decb8f59767acb0cb4e7792f348575a1414721cccf74eb103&=&format=webp&width=385&height=856)) | 
  ![Stats]([https://via.placeholder.com/200x400](https://media.discordapp.net/attachments/1461727943389610114/1488854900573081731/Screenshot_2026-04-01-17-57-01-909_com.example.mureev.jpg?ex=69ce4bad&is=69ccfa2d&hm=a90c8a27acbfeb013786fb805de683f6750422d24ceae98b257ce3eb5c26e72a&=&format=webp&width=385&height=856)) |
