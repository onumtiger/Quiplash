package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton

class New_GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        val btnBack = findViewById<AppCompatImageButton>(R.id.new_game_go_back_arrow)
        val btnStart = findViewById<Button>(R.id.start_game)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnStart.setOnClickListener() {
            val intent = Intent(this, Host_WaitingActivity::class.java);
            startActivity(intent);
        }
    }
}
