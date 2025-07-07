package com.example.mureev

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mureev.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlbumBinding
    private lateinit var adapter: ArtistAlbumAdapter
    private lateinit var originalList: ArrayList<String>
    private var playCounts: Map<String, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.albumToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.albumToolbar.setNavigationOnClickListener {
            finish()
        }

        val showMostPlayed = intent.getBooleanExtra("most_played", false)
        var albumList: List<String>

        if (showMostPlayed) {
            supportActionBar?.title = "Album Terpopuler"
            // Hitung total putar untuk setiap album
            val albumPlayCounts = mutableMapOf<String, Int>()
            MainActivity.playCountMap.forEach { (songId, playCount) ->
                val song = MainActivity.MusicListMA.find { it.id == songId }
                song?.let {
                    albumPlayCounts[it.album] = albumPlayCounts.getOrDefault(it.album, 0) + playCount
                }
            }
            // Urutkan album berdasarkan total putar
            albumList = albumPlayCounts.keys.sortedByDescending { albumPlayCounts[it] ?: 0 }
            this.playCounts = albumPlayCounts
        } else {
            supportActionBar?.title = "Daftar Album"
            // Tampilan normal, urutkan A-Z
            albumList = MainActivity.MusicListMA.map { it.album }.distinct().sorted()
        }

        originalList = ArrayList(albumList)

        // Setup RecyclerView dengan GridLayout
        binding.albumRV.setHasFixedSize(true)
        binding.albumRV.layoutManager = GridLayoutManager(this, 2)
        adapter = ArtistAlbumAdapter(this, ArrayList(albumList), "album")
        binding.albumRV.adapter = adapter
    }

    // Fungsi untuk membuat menu search di toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.queryHint = "Cari Album..."

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