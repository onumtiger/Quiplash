package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameManager.Companion.game

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
        val scoreboardArray = mutableListOf<UserQP>()

        val gameID = game.gameID
        var userIDList = mutableListOf<String>()
        var currentGame: Game
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                currentGame = result
                val players = currentGame.users
                userIDList = players.toMutableList()
                userIDList.forEach {
                    val callbackUser = object : Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            var user = result
                            scoreboardArray.add(user)
                            scoreboardArray.sortWith(Comparator { s1: UserQP, s2: UserQP -> s2.score!! - s1.score!! })
                            println("Final array : ")
                            scoreboardArray.forEach { println(it.score) }
                            val adapter = ScoreboardListAdapter(
                                applicationContext,
                                R.layout.scoreboard_list_item,
                                scoreboardArray
                            )
                            scoreboardList.adapter = adapter
                        }
                    }
                    DBMethods.DBCalls.getUserWithID(callbackUser, it)
                }
            }
        }
        DBMethods.DBCalls.getCurrentGame(callback, gameID)

        val adapter = ScoreboardListAdapter(
            applicationContext,
            R.layout.scoreboard_list_item,
            scoreboardArray
        )
        scoreboardList.adapter = adapter
    }

    override fun onBackPressed() {
        println("do nothing")
    }
}