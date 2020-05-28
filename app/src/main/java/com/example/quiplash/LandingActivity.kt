package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnNewGame = findViewById<Button>(R.id.mainNewGame)
        val btnJoinGame = findViewById<Button>(R.id.mainJoinGame)
        val btnProfile = findViewById<Button>(R.id.mainProfile)
        val btnFriends = findViewById<Button>(R.id.mainFriends)

        btnNewGame.setOnClickListener() {
            val intent = Intent(this, New_GameActivity::class.java);
            startActivity(intent);
        }

        btnJoinGame.setOnClickListener() {
            val intent = Intent(this, Join_GameActivity::class.java);
            startActivity(intent);
        }

        btnProfile.setOnClickListener() {
            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }

        btnFriends.setOnClickListener() {
            val intent = Intent(this, FriendsActivity::class.java);
            startActivity(intent);
        }
    }
}
