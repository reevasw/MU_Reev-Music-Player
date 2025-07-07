package com.example.mureev

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mureev.databinding.ActivityStatsBinding
import java.util.concurrent.TimeUnit

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Statistics"

        setupGeneralStats()
        setupTopArtists()
        setupTopSongs()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Fungsi untuk menangani klik dari adapter
    private fun handleItemClick(item: StatsItem) {
        if (item.type == "song") {
            // Jika yang diklik adalah lagu
            val originalIndex = MainActivity.MusicListMA.indexOfFirst { it.id == item.id }
            if (originalIndex != -1) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("class", "StatsAdapter")
                intent.putExtra("index", originalIndex)
                ContextCompat.startActivity(this, intent, null)
            }
        } else { // "artist"
            // Jika yang diklik adalah artis
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("filter_type", "artist")
            intent.putExtra("filter_name", item.id) // ID untuk artis adalah namanya sendiri
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupGeneralStats() {
        val totalSongs = MainActivity.MusicListMA.size
        binding.statsTotalSongs.text = "Total Lagu: $totalSongs"

        val totalArtists = MainActivity.MusicListMA.map { it.artist }.distinct().size
        binding.statsTotalArtists.text = "Total Artis: $totalArtists"

        val totalAlbums = MainActivity.MusicListMA.map { it.album }.distinct().size
        binding.statsTotalAlbums.text = "Total Album: $totalAlbums"

        var totalDurationMs: Long = 0
        MainActivity.playCountMap.forEach { (songId, playCount) ->
            val song = MainActivity.MusicListMA.find { it.id == songId }
            song?.let {
                totalDurationMs += it.duration * playCount
            }
        }
        val hours = TimeUnit.MILLISECONDS.toHours(totalDurationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalDurationMs) % 60
        binding.statsTotalTime.text = "Total Waktu Putar: ${hours} Jam ${minutes} Menit"
    }

    private fun setupTopArtists() {
        val artistPlayCounts = mutableMapOf<String, Int>()
        MainActivity.playCountMap.forEach { (songId, playCount) ->
            val song = MainActivity.MusicListMA.find { it.id == songId }
            song?.let {
                artistPlayCounts[it.artist] = artistPlayCounts.getOrDefault(it.artist, 0) + playCount
            }
        }
        val sortedArtists = artistPlayCounts.toList().sortedByDescending { it.second }.take(5)

        val topArtistsList = sortedArtists.mapIndexed { index, pair ->
            val artistName = pair.first
            val artUri = MainActivity.MusicListMA.firstOrNull { it.artist == artistName }?.artUri ?: ""
            StatsItem(
                id = artistName, // Gunakan nama artis sebagai ID
                type = "artist",
                rank = index + 1,
                artUri = artUri,
                title = artistName,
                subtitle = "${pair.second} kali diputar"
            )
        }

        binding.topArtistsRV.layoutManager = LinearLayoutManager(this)
        // Saat membuat adapter, kirimkan fungsi handleItemClick
        binding.topArtistsRV.adapter = StatsAdapter(this, topArtistsList) { item -> handleItemClick(item) }
    }

    private fun setupTopSongs() {
        val sortedSongs = MainActivity.playCountMap.toList().sortedByDescending { it.second }.take(5)

        val topSongsList = sortedSongs.mapIndexed { index, pair ->
            val song = MainActivity.MusicListMA.find { it.id == pair.first }
            StatsItem(
                id = song?.id ?: "", // Gunakan ID lagu
                type = "song",
                rank = index + 1,
                artUri = song?.artUri ?: "",
                title = song?.title ?: "Lagu Tidak Ditemukan",
                subtitle = song?.artist ?: ""
            )
        }

        binding.topSongsRV.layoutManager = LinearLayoutManager(this)
        // Saat membuat adapter, kirimkan fungsi handleItemClick
        binding.topSongsRV.adapter = StatsAdapter(this, topSongsList) { item -> handleItemClick(item) }
    }
}
