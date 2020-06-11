package com.example.quiplash

import android.os.CountDownTimer
import android.widget.TextView


class GameMethods {

    class GameCalls {
        companion object {
            lateinit var countdown_timer: CountDownTimer
            var time_in_milli_seconds = 0L

            public fun startTimer(textView :TextView, time_in_seconds :Long) {
                    countdown_timer = object : CountDownTimer(time_in_seconds * 1000, 1000) {
                        override fun onFinish() {
                            //Do something on Fnish!!
                        }
                        override fun onTick(p0: Long) {
                            time_in_milli_seconds = p0
                            updateTextUI(textView)
                        }
                    }
                    countdown_timer.start()
            }

            private fun updateTextUI(textView :TextView) {
                val minute = (time_in_milli_seconds / 1000) / 60
                val seconds = (time_in_milli_seconds / 1000) % 60

                textView.text = "$minute:$seconds"
            }
    
            private fun resetTimer(textView :TextView) {
                time_in_milli_seconds = 60000L
                updateTextUI(textView)
            }

            private fun pauseTimer() {
                countdown_timer.cancel()
            }
        }
    }
}