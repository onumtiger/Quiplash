package com.example.quiplash

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.widget.TextView


class GameMethods {

        companion object {
            lateinit var countdown_timer: CountDownTimer
            var time_in_milli_seconds = 0L

            fun startTimer(textView :TextView, time_in_seconds :Long, callback: Callback<Boolean>) {
                    countdown_timer = object : CountDownTimer(time_in_seconds * 1000, 1000) {
                        override fun onFinish() {
                            callback.onTaskComplete(true)
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

            fun playerAllocation(appcontext: Context, userid: String){
                if(GameManager.game.playrounds.size>= GameManager.game.activeRound) {

                    if (GameManager.game.playrounds[GameManager.game.activeRound - 1].opponents[0].userID.equals(userid) || GameManager.game.playrounds[GameManager.game.activeRound - 1].opponents[1].userID.equals(
                            userid
                        )
                    ) {
                        val intent = Intent(appcontext, PrepareAnswerActivity::class.java)
                        appcontext.startActivity(intent)
                    } else {
                        val intent = Intent(appcontext, ChooseAnswerActivity::class.java)
                        appcontext.startActivity(intent)
                    }
                } else{
                    val intent = Intent(appcontext, LandingActivity::class.java)
                    appcontext.startActivity(intent)
                }
            }
        }
}