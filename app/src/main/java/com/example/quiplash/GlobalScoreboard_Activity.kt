package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.getUsers
import java.util.ArrayList

class GlobalScoreboard_Activity : AppCompatActivity() {
    lateinit var users: ArrayList<UserQP>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_global_scoreboard)

        val btnBack = findViewById<AppCompatImageButton>(R.id.scoreboard_go_back_arrow)

        btnBack.setOnClickListener{
            Sounds.playClickSound(this)

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        val scoreboardList = findViewById<ListView>(R.id.scoreboard_list)

        val callback = object: Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                users = result
                users.sortWith(Comparator { s1: UserQP, s2: UserQP -> s2.score - s1.score })
                val adapter = ScoreboardListAdapter(
                    applicationContext,
                    R.layout.scoreboard_list_item,
                    users
                )
                scoreboardList.adapter = adapter

            }
        }
        getUsers(callback)
    }

}