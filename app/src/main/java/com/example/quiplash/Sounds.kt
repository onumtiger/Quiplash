package com.example.quiplash

import android.content.Context
import android.media.MediaPlayer
import android.widget.AdapterView

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
        fun playStartSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.start)
            mp.start()
        }
        fun playVotingSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.voting)
            mp.start()
        }
        fun playScoreSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.score)
            mp.start()
        }
        fun playAnswerSound(context: Context) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.answer)
            mp.start()
        }
    }
}