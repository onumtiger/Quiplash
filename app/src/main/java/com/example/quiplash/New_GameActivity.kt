package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class New_GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        val btnHome = findViewById<Button>(R.id.btnHome)

        btnHome.setOnClickListener() {
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent);
        }
    }
}
