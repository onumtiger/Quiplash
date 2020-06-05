package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class Host_WaitingActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvite_Players = findViewById<Button>(R.id.invite_players_btn)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnInvite_Players.setOnClickListener(){
            val dialogFragment = Invite_Player()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("invite")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "invite")
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

        if (playerArray.size == 5) {

            Handler().postDelayed({
                val intent = Intent(this, Game_LaunchingActivity::class.java);
                startActivity(intent)
            }, 3000)
        }
    }
}