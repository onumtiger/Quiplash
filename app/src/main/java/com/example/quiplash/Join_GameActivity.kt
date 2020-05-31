package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
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

        val activeGamesList = findViewById<ListView>(R.id.active_games_list)
        val activeGamesArray = arrayOfNulls<String>(5)

        for (i in 0 until activeGamesArray.size) {
            activeGamesArray[i] = "Active Game $i"
        }

        val adapter = ArrayAdapter<String>(
            this, R.layout.active_game_list_item,
            R.id.active_player, activeGamesArray
        )

        activeGamesList.adapter = adapter


    }
}
