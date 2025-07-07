package com.example.mureev

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mureev.databinding.ActivityMostPlayedBinding

class MostPlayedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMostPlayedBinding
    private lateinit var adapter: MostPlayedAdapter

    companion object {
        var mostPlayedSongs: ArrayList<Music> = ArrayList()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityMostPlayedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Lagu Sering Diputar"

        mostPlayedSongs = ArrayList()
        val allSongs = ArrayList(MainActivity.MusicListMA)

        // Mengurutkan lagu berdasarkan jumlah putar
        allSongs.sortByDescending { MainActivity.playCountMap[it.id] ?: 0 }

        // Mengambil semua lagu yang pernah diputar (play count > 0)
        mostPlayedSongs.addAll(allSongs.filter { (MainActivity.playCountMap[it.id] ?: 0) > 0 })

        // Menampilkan pesan jika tidak ada lagu
        if (mostPlayedSongs.isEmpty()) {
            binding.totalSongsMostPlayed.text = "Belum ada lagu yang sering diputar."
        } else {
            binding.totalSongsMostPlayed.text = "Total Lagu : ${mostPlayedSongs.size}"
        }

        binding.mostPlayedRV.setHasFixedSize(true)
        binding.mostPlayedRV.setItemViewCacheSize(10)
        binding.mostPlayedRV.layoutManager = LinearLayoutManager(this)

        // Membuat dan mengatur adapter
        adapter = MostPlayedAdapter(this, mostPlayedSongs)
        binding.mostPlayedRV.adapter = adapter
    }
}