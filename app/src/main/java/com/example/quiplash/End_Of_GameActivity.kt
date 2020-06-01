package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class End_Of_GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_of_game)

        val btnHome = findViewById<Button>(R.id.btnHome)

        btnHome.setOnClickListener() {
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent)
        }

        val scoreboardList = findViewById<ListView>(R.id.scoreboard_list)
        val scoreboardArray = arrayOfNulls<String>(5)

        for (i in 0 until scoreboardArray.size) {
            scoreboardArray[i] = "Player $i"
        }


        val adapter = ArrayAdapter<String>(
            this, R.layout.scoreboard_list_item,
            R.id.active_player, scoreboardArray
        )

        scoreboardList.adapter = adapter
    }
}