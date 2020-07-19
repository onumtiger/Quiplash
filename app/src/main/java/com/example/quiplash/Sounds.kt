package com.example.quiplash

import android.R
import android.content.Context
import android.media.MediaPlayer
import com.example.quiplash.R.*


class Sounds {
    companion object {
        private var mp: MediaPlayer? = null

        /**
         * Sound's played on every successful touch in UI
         */
        fun playClickSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.click)
            mp!!.start()
        }

        /**
         * Sounds's played on refresh of refresh views
         */
        fun playRefreshSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.refresh)
            mp!!.start()
        }

        /**
         * Sounds's played on start of a game round
         */
        fun playStartSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.start)
            mp!!.start()
        }

        /**
         * Sounds's played in voting phase
         */
        fun playVotingSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.voting)
            mp!!.start()
        }

        /**
         * Sounds's played in evaluation
         */
        fun playScoreSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.score)
            mp!!.start()
        }

        /**
         * Sounds's played while preparing an answer
         */
        fun playAnswerSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.prepareanswer)
            mp!!.start()
        }

        /**
         * Sounds's played at the end of a game
         */
        fun playEndSound(context: Context) {
            stopPlaying()
            mp = MediaPlayer.create(context, raw.end)
            mp!!.start()
        }

        /**
         * Stop media player
         */
        private fun stopPlaying() {
            if (mp != null) {
                mp!!.stop()
                mp!!.release()
                mp = null
            }
        }
    }
}