package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class Host_WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        val playersList = findViewById<ListView>(R.id.players_list)
        val playerArray = arrayOfNulls<String>(5)

        for (i in 0 until playerArray.size) {
            playerArray[i] = "player $i"
        }


        val adapter = ArrayAdapter<String>(
            this, R.layout.host_waiting_list_item,
            R.id.active_player, playerArray
        )

        playersList.adapter = adapter
    }
}