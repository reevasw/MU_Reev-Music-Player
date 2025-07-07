package com.example.mureev

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.widget.Toast
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.mureev.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {


    companion object {
        lateinit var musicListPA : ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying:Boolean = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var shuffle: Boolean = false
        var min1: Boolean = false
        var min5: Boolean = false
        var min10: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var min120: Boolean = false
        var min240: Boolean = false
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        private var appVolume: Int = 100
        private var lyricsDialog: BottomSheetDialog? = null
        private var handler: Handler? = null
        private var syncRunnable: Runnable? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.data?.scheme.contentEquals("content")){
            songPosition = 0
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musicListPA[songPosition].path))
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(binding.songImgPA)
            binding.songNamePA.text = musicListPA[songPosition].title
            binding.songAlbumPA.text = musicListPA[songPosition].title
            binding.songArtisPA .text = musicListPA[songPosition].title
        }
        else initializeLayout()

        // Floating Buttons for Previous, Pause & Next, Shuffle & Repeat-->
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtnPA.setIconTint(ContextCompat.getColorStateList(this, R.color.white))
            } else {
                repeat = false
                binding.repeatBtnPA.setIconTint(ContextCompat.getColorStateList(this, R.color.set_white))
            }
        }
        binding.shuffleBtnPA.setOnClickListener {
            val intent = Intent(this@PlayerActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlayerActivity")
            startActivity(intent)

            if (!shuffle) {
                shuffle = true
                binding.shuffleBtnPA.setIconTint(ContextCompat.getColorStateList(this, R.color.white))
            } else {
                shuffle = false
                binding.shuffleBtnPA.setIconTint(ContextCompat.getColorStateList(this, R.color.set_white))
            }
        }
        setButtonScaleEffect(findViewById(R.id.previousBtnPA))
        setButtonScaleEffect(findViewById(R.id.nextBtnPA))

        binding.backBtnPA.setOnClickListener { finish() }
        binding.playPauseBtnPA.setOnClickListener{ if(isPlaying) pauseMusic() else playMusic() }
        binding.previousBtnPA.setOnClickListener { prevNextSong(increment = false) }
        binding.nextBtnPA.setOnClickListener { prevNextSong(increment = true) }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                    musicService!!.showNotification(if(isPlaying) R.drawable.pause_icon else R.drawable.play_icon)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        //   volume, favourite, lyric, speed
        binding.volumeBtnPA.setOnClickListener {
            setButtonScaleEffect(findViewById(R.id.volumeBtnPA))
            showVolumeDialog()
        }

        binding.favouriteBtnPA.setOnClickListener {
            setButtonScaleEffect(findViewById(R.id.favouriteBtnPA))
            fIndex = favouriteChecker(musicListPA[songPosition].id)
            if(isFavourite){
                isFavourite = false
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
                Toast.makeText(this, "Lagu dihapus dari favorit", Toast.LENGTH_SHORT).show()
            } else{
                isFavourite = true
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
                FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
                Toast.makeText(this, "Lagu ditambah ke favorit", Toast.LENGTH_SHORT).show()
            }
            FavouriteActivity.favouritesChanged = true
        }
        binding.lyricBtnPA.setOnClickListener {
            setButtonScaleEffect(findViewById(R.id.lyricBtnPA))
            val currentSongPath = musicListPA[songPosition].path
            showLyrics(currentSongPath)
        }
        binding.speedBtnPA.setOnClickListener {
            setButtonScaleEffect(findViewById(R.id.speedBtnPA))
            showSpeedOptions()
        }

        // Fitur home, equalizer, timer, share, queue
        binding.homeBtnPA.setOnClickListener {
            // Membuat Intent untuk berpindah ke MainActivity
            val intent = Intent(this@PlayerActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                Toast.makeText(this, "Equalizer akan terbuka di layar penuh.", Toast.LENGTH_SHORT).show()
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Fitur Equalizer Tidak Didukung!!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtnPA.setOnClickListener {
            val timer = min5 || min10 || min15 || min30 || min60 || min120 || min240
            if(!timer) showBottomSheetDialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Hentikan Timer")
                    .setMessage("Apakah Anda ingin menghentikan timer?")
                    .setPositiveButton("Ya"){ _, _ ->
                        min1 = false
                        min5 = false
                        min10 = false
                        min15 = false
                        min30 = false
                        min60 = false
                        min120 = false
                        min240 = false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    }
                    .setNegativeButton("Tidak"){dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                setDialogBtnBackground(this, customDialog)
            }
        }
        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Berbagi File Musik!"))

        }
        binding.queueBtnPA.setOnClickListener {
            val queueDialog = QueueBottomSheet()
            queueDialog.show(supportFragmentManager, "QueueDialog")
        }
    }

    // Fungsi showLyrics sekarang hanya untuk membuka dialog pertama kali
    private fun showLyrics(audioFilePath: String) {
        try {
            val audioFile: AudioFile = AudioFileIO.read(File(audioFilePath))
            val lyricsText = audioFile.tag.getFirst(FieldKey.LYRICS)

            if (lyricsText.isNotEmpty()) {
                val parsedLyrics = parseLrc(lyricsText)
                if (parsedLyrics.isNotEmpty()) {
                    setupKaraokeDialog(parsedLyrics)
                } else {
                    showLyricsSearchDialog(musicListPA[songPosition].artist, musicListPA[songPosition].title)
                }
            } else {
                showLyricsSearchDialog(musicListPA[songPosition].artist, musicListPA[songPosition].title)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat lirik", Toast.LENGTH_LONG).show()
        }
    }

    // Fungsi setupKaraokeDialog sekarang berisi logika sinkronisasi yang lebih baik
    private fun setupKaraokeDialog(lyricLines: ArrayList<LyricLine>) {
        handler?.removeCallbacksAndMessages(null)

        val view = layoutInflater.inflate(R.layout.dialog_lyrics, null)
        val binding = com.example.mureev.databinding.DialogLyricsBinding.bind(view)

        lyricsDialog = BottomSheetDialog(this, R.style.TransparentBottomSheetDialogTheme)
        lyricsDialog?.setContentView(view)

        // Atur tinggi dialog saat ditampilkan
        lyricsDialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val desiredHeight = 400.toPx() // Atur tinggi dialog di sini (misal: 400dp)
                behavior.peekHeight = desiredHeight
            }
        }

        lyricsDialog?.setOnDismissListener {
            handler?.removeCallbacksAndMessages(null)
        }

        binding.lyricsSongTitle.text = musicListPA[songPosition].title
        val lyricsRV = binding.lyricsRV
        val layoutManager = LinearLayoutManager(this)
        val lyricsAdapter = LyricsAdapter(lyricLines) { time ->
            // Logika untuk tap-to-seek
            handler?.removeCallbacks(syncRunnable!!)
            musicService?.mediaPlayer?.seekTo(time.toInt())
            val tappedIndex = lyricLines.indexOfFirst { it.time == time }
            if (tappedIndex != -1) {
                (lyricsRV.adapter as LyricsAdapter).setCurrentLine(tappedIndex)
                // Langsung scroll ke posisi yang dituju
                layoutManager.scrollToPositionWithOffset(tappedIndex, lyricsRV.height / 2 - 50)
            }
            handler?.postDelayed(syncRunnable!!, 1000)
        }

        lyricsRV.layoutManager = layoutManager
        lyricsRV.adapter = lyricsAdapter

        handler = Handler(Looper.getMainLooper())
        syncRunnable = object : Runnable {
            override fun run() {
                try {
                    val currentPosition = musicService!!.mediaPlayer!!.currentPosition.toLong()
                    val currentIndex = lyricLines.indexOfLast { it.time <= currentPosition }

                    // Hanya scroll jika baris aktifnya benar-benar berubah
                    if (currentIndex != -1 && lyricsAdapter.getCurrentLineIndex() != currentIndex) {
                        lyricsAdapter.setCurrentLine(currentIndex)

                        // ▼▼▼ LOGIKA SCROLL BARU YANG LEBIH BAIK ▼▼▼
                        val smoothScroller = object : LinearSmoothScroller(this@PlayerActivity) {
                            override fun getVerticalSnapPreference(): Int {
                                // Opsi ini memberikan kontrol penuh pada kalkulasi scroll
                                return SNAP_TO_ANY
                            }

                            // Fungsi ini menghitung jarak yang tepat untuk membuat item berada di tengah
                            override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int {
                                val recyclerViewCenter = lyricsRV.height / 2
                                val itemCenter = view.top + (view.height / 2)
                                return recyclerViewCenter - itemCenter
                            }
                        }

                        smoothScroller.targetPosition = currentIndex
                        layoutManager.startSmoothScroll(smoothScroller)
                        // ▲▲▲ AKHIR DARI LOGIKA SCROLL BARU ▲▲▲
                    }
                    handler?.postDelayed(this, 250) // Ulangi setiap 250ms
                } catch (e: Exception) {
                    // Abaikan error jika activity sudah ditutup
                }
            }
        }

        lyricsDialog?.show()
        handler?.postDelayed(syncRunnable!!, 0)
    }

    // Tambahkan fungsi parseLrc ini
    private fun parseLrc(lrcText: String): ArrayList<LyricLine> {
        val lyricLines = ArrayList<LyricLine>()
        val regex = """\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""".toRegex()
        lrcText.lines().forEach { line ->
            regex.find(line)?.let {
                val (min, sec, ms, text) = it.destructured
                val time = min.toLong() * 60000 + sec.toLong() * 1000 + ms.toLong()
                if (text.isNotBlank()) {
                    lyricLines.add(LyricLine(time, text.trim()))
                }
            }
        }
        lyricLines.sortBy { it.time }
        return lyricLines
    }

    private fun showLyricsSearchDialog(songArtists: String, songTitle: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Lirik Tidak Tersedia")
        builder.setMessage("Ingin mencari lirik lagu \"$songTitle\" oleh $songArtists di Musixmatch?")

        builder.setPositiveButton("Ya") { _, _ ->
            // Format nama artis dan judul lagu agar cocok dengan URL Musixmatch
            val artist = songArtists.trim().replace(" ", "-")
            val title = songTitle.trim().replace(" ", "-")
            val url = "https://www.musixmatch.com/lyrics/$artist/$title"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    // Fungsi untuk menampilkan PopupMenu dengan opsi kecepatan
    private fun showSpeedOptions() {
        val popupMenu = PopupMenu(this, binding.speedBtnPA)
        popupMenu.menu.apply {
            add("0.5x")
            add("0.75x")
            add("Normal")
            add("1.25x")
            add("1.75x")
            add("2x")
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "0.5x" -> setPlaybackSpeed(0.5f)
                "0.75x" -> setPlaybackSpeed(0.75f)
                "Normal" -> setPlaybackSpeed(1.0f)
                "1.25x" -> setPlaybackSpeed(1.25f)
                "1.75x" -> setPlaybackSpeed(1.75f)
                "2x" -> setPlaybackSpeed(2.0f)
            }
            true
        }
        popupMenu.show()
    }

    // Fungsi untuk mengatur kecepatan playback pada MediaPlayer
    private fun setPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            musicService?.mediaPlayer?.let { mediaPlayer ->
                val playbackParams = mediaPlayer.playbackParams
                playbackParams.speed = speed
                mediaPlayer.playbackParams = playbackParams
                Toast.makeText(this, "Kecepatan pemutaran diatur ke ${speed}x", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Penyesuaian kecepatan pemutaran tidak didukung pada perangkat ini", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showVolumeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_volume, null)
        val volumeSeekBar = dialogView.findViewById<SeekBar>(R.id.volumeSeekBar)
        val volumeLabel = dialogView.findViewById<TextView>(R.id.volumeLabel)

        // Set nilai awal SeekBar ke nilai volume aplikasi
        volumeSeekBar.progress = appVolume
        volumeLabel.text = "  Volume: $appVolume%"

        // Tambahkan listener ke SeekBar untuk mengatur volume aplikasi
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                appVolume = progress
                volumeLabel.text = "  Volume: $progress%"
                musicService?.setVolume(progress) //
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Tampilkan dialog untuk mengatur volume
        AlertDialog.Builder(this)
            .setTitle("Atur Volume")
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }
    //Important Function
    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MainActivity" -> {
                musicListPA = MainActivity.MusicListMA
                if (shuffle) musicListPA.shuffle()
            }
            "QueueDialogFragment" -> {
                musicListPA = MainActivity.MusicListMA
                setLayout()
                createMediaPlayer()
            }
            "NowPlaying"->{
                setLayout()
                binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying) binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                else binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            }
            "MusicAdapterSearch"-> initServiceAndPlaylist(MainActivity.musicListSearch, shuffle = false)
            "MusicAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
            "FavouriteAdapter"-> initServiceAndPlaylist(FavouriteActivity.favouriteSongs, shuffle = false)
            "PlaylistDetailsAdapter"-> initServiceAndPlaylist(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist, shuffle = false)
            "PlaylistDetailsShuffle"-> initServiceAndPlaylist(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist, shuffle = true)
            "PlayNext"->initServiceAndPlaylist(PlayNext.playNextList, shuffle = false, playNext = true)
            "FavouriteShuffle"-> initServiceAndPlaylist(FavouriteActivity.favouriteSongs, shuffle = true)
            "MostPlayedAdapter"-> initServiceAndPlaylist(MostPlayedActivity.mostPlayedSongs, shuffle = false)
            "DetailAdapter" -> initServiceAndPlaylist(DetailActivity.songList, shuffle = false)
            "StatsAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
        }
        if (musicService!= null && !isPlaying) playMusic()
    }

    private fun setLayout() {
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(binding.songImgPA)

        if (lyricsDialog?.isShowing == true) {
            try {
                val audioFile: AudioFile = AudioFileIO.read(File(musicListPA[songPosition].path))
                val lyricsText = audioFile.tag.getFirst(FieldKey.LYRICS)
                val parsedLyrics = parseLrc(lyricsText)

                if (parsedLyrics.isNotEmpty()) {
                    // Jika lagu baru punya lirik, update dialog
                    lyricsDialog?.dismiss() // Tutup yang lama
                    setupKaraokeDialog(parsedLyrics) // Buka yang baru dengan lirik baru
                } else {
                    // Jika lagu baru tidak punya lirik, tutup dialog
                    lyricsDialog?.dismiss()
                    Toast.makeText(this, "Lirik tidak tersedia untuk lagu ini", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Jika ada error saat membaca file baru, tutup dialog
                lyricsDialog?.dismiss()
            }
        }

        binding.songNamePA.text = musicListPA[songPosition].title
        binding.songNamePA.isSelected = true //aktifkan teks bergerak
        binding.songAlbumPA.text = musicListPA[songPosition].album
        binding.songAlbumPA.isSelected = true
        binding.songArtisPA.text = musicListPA[songPosition].artist
        binding.songArtisPA.isSelected = true

        try {
            val currentSong = musicListPA[songPosition]
            val file = java.io.File(currentSong.path)
            val audioFile = org.jaudiotagger.audio.AudioFileIO.read(file)
            val header = audioFile.audioHeader

            val format = header.format.uppercase()
            val sampleRate = "${header.sampleRateAsNumber / 1000.0}kHz"
            val bitDepth = "${header.bitsPerSample}bit"
            val bitrate = "${header.bitRate}Kbps"

            binding.audioInfoTV.text = "$format: $sampleRate  $bitDepth  $bitrate"
            binding.audioInfoTV.visibility = View.VISIBLE
        } catch (e: Exception) {
            // Jika gagal membaca info, sembunyikan TextView
            binding.audioInfoTV.visibility = View.GONE
            e.printStackTrace()
        }

        if (repeat) binding.repeatBtnPA.setIconTint(
            ContextCompat.getColorStateList(
                applicationContext,
                R.color.white
            )
        )
        if (shuffle) binding.shuffleBtnPA.setIconTint(
            ContextCompat.getColorStateList(
                applicationContext,
                R.color.white
            )
        )
        if (min1 || min10 || min15 || min30 || min60 || min120 || min240) binding.timerBtnPA.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                R.color.cool_blue
            )
        )
        if (isFavourite) binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)

        val img = getImgArt(musicListPA[songPosition].path)
        val image = if (img != null) {
            BitmapFactory.decodeByteArray(img, 0, img.size)
        } else {
            BitmapFactory.decodeResource(
                resources,
                R.drawable.music_player_icon_slash_screen
            )
        }

        // Mengambil warna dominan menggunakan Palette
        Palette.from(image).generate { palette ->
            palette?.let {
                // Ambil dua warna dominan
                val color1 = it.getDominantColor(0xFFFFFF)
                var color2 = it.getVibrantColor(0xFFFFFF)

                // Cek apakah color1 dan color2 sama
                if (color1 == color2) {
                    // Jika sama, coba ambil warna lain seperti warna gelap atau terang
                    color2 = it.getDarkVibrantColor(0xFFFFFF)
                    if (color1 == color2) {
                        color2 =
                            it.getLightVibrantColor(0xFFFFFF)
                    }
                }

                // Jika tetap sama, tetapkan warna default untuk color2
                if (color1 == color2) {
                    color2 = 0xCCCCCC // Misalnya warna abu-abu default
                }

                // Buat gradien dari kiri ke kanan
                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(color1, color2)
                )
                binding.root.background = gradient

                // Ubah warna status bar agar sesuai dengan warna dominan
                window?.statusBarColor = color1
            }
        }
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
            playMusic()

            // Logika untuk menghitung jumlah putar dijalankan langsung
            val songId = musicListPA[songPosition].id
            var count = MainActivity.playCountMap.getOrDefault(songId, 0)
            count++
            MainActivity.playCountMap[songId] = count

        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun playMusic(){
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean) {
        if (shuffle) {
            // Jika shuffle aktif, pilih lagu secara acak
            songPosition = (0 until musicListPA.size).random()
        } else {
            // Jika shuffle tidak aktif, gunakan urutan biasa
            setSongPosition(increment = increment)
        }
        setLayout()
        createMediaPlayer()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if(musicService == null){
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        setLayout()

        // Refresh tampilan "Now Playing" saat lagu selesai
        NowPlaying.binding.songNameNP.isSelected = true

        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(NowPlaying.binding.songImgNP)

        NowPlaying.binding.songNameNP.text = musicListPA[songPosition].title
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK)
            return
    }

    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_1)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 1 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min1 = true
            Thread{Thread.sleep((1 * 60000).toLong())
                if(min1) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_5)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 5 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min5 = true
            Thread{Thread.sleep((5 * 60000).toLong())
                if(min5) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_10)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 10 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min10 = true
            Thread{Thread.sleep((10 * 60000).toLong())
                if(min10) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 15 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min15 = true
            Thread{Thread.sleep((15 * 60000).toLong())
                if(min15) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 30 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min30 = true
            Thread{Thread.sleep((30 * 60000).toLong())
                if(min30) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 60 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min60 = true
            Thread{Thread.sleep((60 * 60000).toLong())
                if(min60) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_120)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 120 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min120 = true
            Thread{Thread.sleep((120 * 60000).toLong())
                if(min120) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_240)?.setOnClickListener {
            Toast.makeText(baseContext,  "Musik akan berhenti setelah 240 menit", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            min240 = true
            Thread{Thread.sleep((240 * 60000).toLong())
                if(min240) exitApplication()}.start()
            dialog.dismiss()
        }
    }

    private fun setButtonScaleEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.4f).scaleY(1.4f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }
    }

    fun playSongFromQueue(position: Int) {
        // Pastikan posisi yang dipilih berbeda dengan yang sedang diputar
        if (songPosition != position) {
            // Atur posisi lagu yang baru
            songPosition = position

            // Buat ulang media player untuk lagu yang baru
            createMediaPlayer()

            // Perbarui seluruh UI di PlayerActivity
            setLayout()
        }
    }

    private fun getMusicDetails(contentUri: Uri): Music {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return Music(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
                artUri = "Unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicListPA[songPosition].id == "Unknown" && !isPlaying) exitApplication()
    }
    private fun initServiceAndPlaylist(playlist: ArrayList<Music>, shuffle: Boolean, playNext: Boolean = false){
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicListPA = ArrayList()
        musicListPA.addAll(playlist)
        if(shuffle) musicListPA.shuffle()
        setLayout()
        if(!playNext) PlayNext.playNextList = ArrayList()
    }
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}