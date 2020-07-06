package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.database.DBMethods.Companion.updateUserScores
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.LandingActivity
import com.example.quiplash.R
import com.example.quiplash.scoreboard.ScoreboardListAdapter
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP

class EndOfGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_end_of_game)

        Sounds.playEndSound(this)

        val btnHome = findViewById<Button>(R.id.btnHome)

        btnHome.setOnClickListener {
            Sounds.playClickSound(this)
            deleteGame()
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
                    val callbackUser = object :
                        Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            val user = result
                            println(user.userID)
                            var score = 0
                            currentGame.playrounds.forEach {
                                it.value.opponents.forEach {
                                    if (it.value.userID == user.userID) {
                                        user.score = score + it.value.answerScore
                                        score = user.score
                                    }
                                }
                            }

                            user.score = score
                            scoreboardArray.add(user)
                            scoreboardArray.sortWith(Comparator { s1: UserQP, s2: UserQP -> s2.score - s1.score })
                            val adapter =
                                ScoreboardListAdapter(
                                    applicationContext,
                                    R.layout.scoreboard_list_item,
                                    scoreboardArray
                                )
                            scoreboardList.adapter = adapter
                            updateUserScores(user.userID, score)
                        }
                    }
                    DBMethods.getUserWithID(callbackUser, it)
                }
            }
        }
        DBMethods.getCurrentGame(callback, gameID)

        val adapter = ScoreboardListAdapter(
            applicationContext,
            R.layout.scoreboard_list_item,
            scoreboardArray
        )
        scoreboardList.adapter = adapter
    }

    private fun deleteGame(){
        val callbackSuccess = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                val intent = Intent(this@EndOfGameActivity, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        DBMethods.deleteGame(game.gameID,callbackSuccess)
    }

    override fun onBackPressed() {
        println("do nothing")
    }
}