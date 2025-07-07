package com.example.mureev

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.mureev.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Setting"
        when(MainActivity.themeIndex){
            0 -> binding.coolPinkTheme.setBackgroundColor(Color.parseColor("#DCDDDC"))
            1 -> binding.coolBlueTheme.setBackgroundColor(Color.parseColor("#DCDDDC"))
            2 -> binding.coolPurpleTheme.setBackgroundColor(Color.parseColor("#DCDDDC"))
            3 -> binding.coolGreenTheme.setBackgroundColor(Color.parseColor("#DCDDDC"))
            4 -> binding.coolBlackTheme.setBackgroundColor(Color.parseColor("#DCDDDC"))
        }
        binding.coolPinkTheme.setOnClickListener { saveTheme(0) }
        binding.coolBlueTheme.setOnClickListener { saveTheme(1) }
        binding.coolPurpleTheme.setOnClickListener { saveTheme(2) }
        binding.coolGreenTheme.setOnClickListener { saveTheme(3) }
        binding.coolBlackTheme.setOnClickListener { saveTheme(4) }
        binding.versionName.text = setVersionDetails()
        binding.sortBtn.setOnClickListener {
            val menuList = arrayOf("Baru Ditambahkan", "Judul Lagu", "Ukuran File")
            var currentSort = MainActivity.sortOrder
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sorting")
                .setPositiveButton("OK"){ _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort){ _,which->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }

        binding.clearCacheBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Cache")
                .setMessage("Tindakan ini akan menghapus semua gambar sampul yang tersimpan sementara. Gambar akan dimuat ulang saat dibutuhkan. Lanjutkan?")
                .setPositiveButton("Ya") { _, _ ->
                    clearAppCache()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun saveTheme(index: Int){
        if(MainActivity.themeIndex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Terapkan Tema")
                .setMessage("Apakah Anda ingin menerapkan tema?")
                .setPositiveButton("Ya"){ _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                .setNegativeButton("Tidak"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }
    }
    private fun setVersionDetails():String{
        return "Versi Aplikasi ${BuildConfig.VERSION_NAME}"
    }

    private fun clearAppCache() {
        // Membersihkan cache disk di background thread agar tidak memblokir UI
        Thread {
            Glide.get(applicationContext).clearDiskCache()
        }.start()

        // Membersihkan cache memori di UI thread
        runOnUiThread {
            Glide.get(applicationContext).clearMemory()
            Toast.makeText(this, "Cache berhasil dibersihkan", Toast.LENGTH_SHORT).show()
        }
    }
}
