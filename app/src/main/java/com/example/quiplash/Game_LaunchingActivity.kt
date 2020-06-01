package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Game_LaunchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_launching)

        Handler().postDelayed({
            val intent = Intent(this, QuestionActivity::class.java);
            startActivity(intent)
        }, 3000)
    }
}