package com.example.mureev

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.example.mureev.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<Music>
        lateinit var musicListSearch : ArrayList<Music>
        var search: Boolean = false
        var themeIndex: Int = 0
        val currentTheme = arrayOf(R.style.coolPink, R.style.coolBlue, R.style.coolPurple, R.style.coolGreen, R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav, R.style.coolBlackNav)
        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green, R.drawable.gradient_black)
        var sortOrder: Int = 0
        var playCountMap: MutableMap<String, Int> = mutableMapOf()
        var playTimeMap: MutableMap<String, Long> = mutableMapOf()
        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE + " DESC")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentThemeNav[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root,R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //checking for dark theme
        if(themeIndex == 4 &&  resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO)
            Toast.makeText(this, "Tema Ireng Berfungsi Paling Baik dalam Mode Gelap!!", Toast.LENGTH_LONG).show()

        if(requestRuntimePermission()){
            initializeLayout()

            // Memuat data Favourites & Playlists
            FavouriteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouriteActivity.favouriteSongs.addAll(data)
            }
            PlaylistActivity.musicPlaylist = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if(jsonStringPlaylist != null){
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist = dataPlaylist
            }

            // Memuat data Jumlah Putar (Play Counts)
            val playCountPrefs = getSharedPreferences("PLAY_COUNTS", MODE_PRIVATE)
            val jsonPlayCount = playCountPrefs.getString("PlayCountsMap", null)
            if(jsonPlayCount != null) {
                val data: MutableMap<String, Int> = GsonBuilder().create().fromJson(jsonPlayCount, object : TypeToken<MutableMap<String, Int>>(){}.type)
                playCountMap = data
            }

            // Memuat data Waktu Putar (Play Times)
            val playTimePrefs = getSharedPreferences("PLAY_TIMES", MODE_PRIVATE)
            val jsonPlayTime = playTimePrefs.getString("PlayTimeMap", null)
            if(jsonPlayTime != null) {
                val data: MutableMap<String, Long> = GsonBuilder().create().fromJson(jsonPlayTime, object : TypeToken<MutableMap<String, Long>>(){}.type)
                playTimeMap = data
            }
        }

        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.MostPlayedBtn.setOnClickListener {
            val options = arrayOf("Lagu", "Artis", "Album")
            MaterialAlertDialogBuilder(this)
                .setTitle("Tampilkan Terpopuler Berdasarkan")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> startActivity(Intent(this, MostPlayedActivity::class.java)) // Lagu
                        1 -> { // Artis
                            val intent = Intent(this, ArtistActivity::class.java)
                            intent.putExtra("most_played", true)
                            startActivity(intent)
                        }
                        2 -> { // Album
                            val intent = Intent(this, AlbumActivity::class.java)
                            intent.putExtra("most_played", true)
                            startActivity(intent)
                        }
                    }
                }
                .show()
        }
        binding.scrollUpBtn.setOnClickListener {
            binding.scrollBar.smoothScrollToPosition(0)
        }
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId)
            {
                R.id.navUser -> startActivity(Intent(this@MainActivity, UserActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                R.id.navStats -> startActivity(Intent(this, StatsActivity::class.java))
                R.id.navAbout -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.navLogout -> {
                    val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Kamu ingin keluar dari Aplikasi?")
                        .setPositiveButton("Ya"){ _, _ ->
                            exitApplication()
                        }
                        .setNegativeButton("Tidak"){dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()

                    setDialogBtnBackground(this, customDialog)
                }
            }
            true
        }
    }
    //For requesting permission
    private fun requestRuntimePermission() :Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 13)
                return false
            }
        }else{
            //android 13 or Higher permission request
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted",Toast.LENGTH_SHORT).show()
                initializeLayout()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){
        search = false
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)
        MusicListMA = getAllAudio()
        binding.scrollBar.setHasFixedSize(true)
        binding.scrollBar.setItemViewCacheSize(13)
        binding.scrollBar.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.scrollBar.adapter = musicAdapter
        updateMusicSummary()

        //for refreshing layout on swipe from top
        binding.refreshLayout.setOnRefreshListener {
            MusicListMA = getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)

            binding.refreshLayout.isRefreshing = false
        }
    }

    @SuppressLint("Recycle", "Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()

        // Filter Only Music or Audio Files
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.MIME_TYPE + " LIKE 'audio/%'"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            sortingList[sortOrder], null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)) ?: "Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    // Only add the music file if the duration is greater than 0
                    if (durationC > 0) {
                        val music = Music(
                            id = idC,
                            title = titleC,
                            album = albumC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUriC
                        )

                        if (File(music.path).exists()) tempList.add(music)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }

    private fun updateMusicSummary() {
        val totalLagu = MusicListMA.size
        val totalArtis = MusicListMA.map { it.artist }.distinct().size
        val totalAlbum = MusicListMA.map { it.album }.distinct().size

        // Buat string untuk setiap bagian
        val laguStr = "$totalLagu Lagu"
        val artisStr = "$totalArtis Artis"
        val albumStr = "$totalAlbum Album"

        // Gabungkan dengan pemisah
        val fullText = "$laguStr • $artisStr • $albumStr"
        val spannableString = SpannableString(fullText)

        // Buat span yang bisa diklik untuk Artis
        val artistClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, ArtistActivity::class.java))
            }
        }

        // Buat span yang bisa diklik untuk Album
        val albumClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, AlbumActivity::class.java))
            }
        }

        // Terapkan span ke bagian teks yang benar
        val artisStart = fullText.indexOf(artisStr)
        val artisEnd = artisStart + artisStr.length
        spannableString.setSpan(artistClickableSpan, artisStart, artisEnd, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        val albumStart = fullText.indexOf(albumStr)
        val albumEnd = albumStart + albumStr.length
        spannableString.setSpan(albumClickableSpan, albumStart, albumEnd, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set teks dan aktifkan agar link bisa diklik
        binding.totalSongs.text = spannableString
        binding.totalSongs.movementMethod = LinkMovementMethod.getInstance()
        binding.totalSongs.highlightColor = Color.TRANSPARENT // Agar tidak ada highlight saat diklik
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()

        // Menyimpan data Favourites & Playlists
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

        // Menyimpan data Jumlah Putar
        val playCountEditor = getSharedPreferences("PLAY_COUNTS", MODE_PRIVATE).edit()
        val jsonPlayCount = GsonBuilder().create().toJson(playCountMap)
        playCountEditor.putString("PlayCountsMap", jsonPlayCount)
        playCountEditor.apply()

        // Menyimpan data Waktu Putar
        val playTimeEditor = getSharedPreferences("PLAY_TIMES", MODE_PRIVATE).edit()
        val jsonPlayTime = GsonBuilder().create().toJson(playTimeMap)
        playTimeEditor.putString("PlayTimeMap", jsonPlayTime)
        playTimeEditor.apply()

        //for sorting
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder", 0)
        if(sortOrder != sortValue){
            sortOrder = sortValue
            MusicListMA = getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)
        }
        if(PlayerActivity.musicService != null) binding.nowPlaying.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        //for setting gradient
        findViewById<LinearLayout>(R.id.linearLayoutNav)?.setBackgroundResource(currentGradient[themeIndex])

        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MusicListMA) {
                        // Periksa apakah judul, artis, atau album mengandung teks yang dicari
                        if (song.title.lowercase().contains(userInput) ||
                            song.artist.lowercase().contains(userInput) ||
                            song.album.lowercase().contains(userInput)
                        ) {
                            musicListSearch.add(song)
                        }
                    }
                    search = true
                    musicAdapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}