package com.example.mureev

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mureev.databinding.ActivityArtistBinding

class ArtistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtistBinding
    private lateinit var adapter: ArtistAlbumAdapter
    private lateinit var originalList: ArrayList<String>
    private var playCounts: Map<String, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.artistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.artistToolbar.setNavigationOnClickListener {
            finish()
        }

        val showMostPlayed = intent.getBooleanExtra("most_played", false)
        var artistList: List<String>

        if (showMostPlayed) {
            supportActionBar?.title = "Artis Terpopuler"
            // Hitung total putar untuk setiap artis
            val artistPlayCounts = mutableMapOf<String, Int>()
            MainActivity.playCountMap.forEach { (songId, playCount) ->
                val song = MainActivity.MusicListMA.find { it.id == songId }
                song?.let {
                    artistPlayCounts[it.artist] = artistPlayCounts.getOrDefault(it.artist, 0) + playCount
                }
            }
            // Urutkan artis berdasarkan total putar
            artistList = artistPlayCounts.keys.sortedByDescending { artistPlayCounts[it] ?: 0 }
            this.playCounts = artistPlayCounts
        } else {
            supportActionBar?.title = "Daftar Artis"
            // Tampilan normal, urutkan A-Z
            artistList = MainActivity.MusicListMA.map { it.artist }.distinct().sorted()
        }

        originalList = ArrayList(artistList)

        // Setup RecyclerView dengan GridLayout
        binding.artistRV.setHasFixedSize(true)
        binding.artistRV.layoutManager = GridLayoutManager(this, 2)
        adapter = ArtistAlbumAdapter(this, ArrayList(artistList), "artist")
        binding.artistRV.adapter = adapter
    }

    // Fungsi untuk membuat menu search di toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.queryHint = "Cari Artist..."

        // Mengambil komponen di dalam SearchView
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val searchText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)

        // Mengubah warna ikon dan teks menjadi putih
        searchIcon.setColorFilter(Color.WHITE)
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.LTGRAY)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = originalList.filter {
                    it.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateList(ArrayList(filteredList))
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}