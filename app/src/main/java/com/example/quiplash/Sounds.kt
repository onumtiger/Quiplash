package com.example.quiplash

import android.content.Context
import android.media.MediaPlayer

class Sounds {
    companion object {
        fun playClickSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.click)
            mp.start()
        }
        fun playRefreshSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.refresh)
            mp.start()
        }
    }
}