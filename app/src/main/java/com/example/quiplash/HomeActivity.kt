package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val btnNewGame = findViewById<Button>(R.id.mainNewGame)
        val btnJoinGame = findViewById<Button>(R.id.mainJoinGame)
        val btnProfile = findViewById<Button>(R.id.mainProfile)
        val btnFriends = findViewById<Button>(R.id.mainFriends)

        btnNewGame.setOnClickListener() {
            val intent = Intent(this, NewGameCategoryActivity::class.java);
            startActivity(intent);
        }

        btnJoinGame.setOnClickListener() {
            val intent = Intent(this, JoinGameActivity::class.java);
            startActivity(intent);
        }

        btnProfile.setOnClickListener() {
            val intent = Intent(this, ProfileActivity::class.java);
            startActivity(intent);
        }

        btnFriends.setOnClickListener() {
            val intent = Intent(this, FriendsActivity::class.java);
            startActivity(intent);
        }
    }
}
