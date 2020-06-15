package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameMethods.GameCalls.Companion.startTimer

import kotlinx.android.synthetic.main.activity_main.*

class Choose_AnswerActivity : AppCompatActivity() {

    lateinit var timerView :TextView

    var START_MILLI_SECONDS = 60000L

    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_answer)


        timerView = findViewById(R.id.timer2)
        var othertimer = findViewById<TextView>(R.id.timer3)
        othertimer.visibility = View.INVISIBLE

        startTimer(timerView, 18)
    }

    override fun onBackPressed() {
        println("do nothing")
    }
}