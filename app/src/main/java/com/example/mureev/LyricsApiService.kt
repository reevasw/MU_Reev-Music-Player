package com.example.mureev

import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class LyricsApiService {
    private val client = OkHttpClient()

    interface LyricsCallback {
        fun onSuccess(lyrics: String)
        fun onFailure(error: String)
    }

    fun fetchLyrics(artist: String, title: String, callback: LyricsCallback) {
        // Menggunakan LRCLIB API (Gratis dan tidak perlu API Key untuk pencarian publik)
        val url = "https://lrclib.net/api/get?artist=${artist.replace(" ", "%20")}&track=${title.replace(" ", "%20")}"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val json = org.json.JSONObject(body)
                        // Coba ambil syncedLyrics (LRC) dulu, jika tidak ada ambil plainLyrics
                        val lyrics = if (json.has("syncedLyrics") && !json.isNull("syncedLyrics")) {
                            json.getString("syncedLyrics")
                        } else if (json.has("plainLyrics") && !json.isNull("plainLyrics")) {
                            json.getString("plainLyrics")
                        } else {
                            null
                        }

                        if (lyrics != null) {
                            callback.onSuccess(lyrics)
                        } else {
                            callback.onFailure("Lyrics not found")
                        }
                    } catch (e: Exception) {
                        callback.onFailure("Parsing error")
                    }
                } else {
                    callback.onFailure("Lyrics not found on server")
                }
            }
        })
    }
}
