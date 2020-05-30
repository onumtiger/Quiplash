package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton

class Join_GameActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val btnNewGameActivity = findViewById<AppCompatImageButton>(R.id.join_new_game_btn)
        val btnBack = findViewById<AppCompatImageButton>(R.id.join_game_go_back_arrow)

        btnNewGameActivity.setOnClickListener() {
            val intent = Intent(this, New_GameActivity::class.java);
            startActivity(intent);
        }

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }


    }
}
