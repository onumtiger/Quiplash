package com.example.quiplash

import android.R
import android.content.Context
import android.media.MediaPlayer
import com.example.quiplash.R.*


class Sounds {
    companion object {
            // var mp: MediaPlayer = MediaPlayer()
            var mp: MediaPlayer? = null

        fun playClickSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.click)
            mp!!.start()
        }
        fun playRefreshSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.refresh)
            mp!!.start()
        }
        fun playStartSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.start)
            mp!!.start()
        }
        fun playVotingSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.voting)
            mp!!.start()
        }
        fun playScoreSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.score)
            mp!!.start()
        }
        fun playAnswerSound(context: Context) {
            stopPlaying()
            // hier muss ein anderer sound rein
            //mp = MediaPlayer.create(context, raw.answer)
            //mp!!.start()
        }
        fun playEndSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.end)
            mp!!.start()
        }
        fun stopPlaying() {
            if (mp != null) {
                mp!!.stop()
                mp!!.release()
                mp = null
            }
        }
    }
}