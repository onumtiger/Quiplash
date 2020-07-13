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
import com.example.quiplash.database.DBMethods.Companion.editUser
import com.example.quiplash.database.DBMethods.Companion.getUsers
import com.example.quiplash.game.GameManager.Companion.gameSecondScore
import com.example.quiplash.game.GameManager.Companion.gameThirdScore
import com.example.quiplash.game.GameManager.Companion.gameWinnerScore
import com.example.quiplash.user.UserQP
import java.util.ArrayList

class EndOfGameActivity : AppCompatActivity() {

    var all_user = arrayListOf<UserQP>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_end_of_game)

        val btnHome = findViewById<Button>(R.id.btnHome)

        Sounds.playEndSound(this)

        btnHome.setOnClickListener {
            Sounds.playClickSound(this)
            deleteGame()
        }

        val callbackGetUsers = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                all_user = result
            }
        }
        getUsers(callbackGetUsers)

        showPlayerScores()
    }

    /**
     * delete game that ended from db
     */
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

    private fun showPlayerScores() {
        val scoreboardList = findViewById<ListView>(R.id.scoreboard_list)
        val scoreboardArray = mutableListOf<UserQP>()
        val gameID = game.gameID
        var userIDList = mutableListOf<String>()
        var currentGame: Game
        var old_scores = arrayListOf<Int>()
        var users_here = hashMapOf<UserQP, Int>()

        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                currentGame = result
                val players = currentGame.users
                userIDList = players.toMutableList()

                // get hash with game players and old scores
                players.forEach {
                    var current_id = it
                    for (x in 0..all_user.size-1){
                        if (all_user[x].userID == current_id){
                            users_here.put(all_user[x], all_user[x].score)
                        }
                    }
                }

                userIDList.forEach {
                    val callbackUser = object :
                        Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            val user = result
                            var score = 0

                            // sum each player's points of all rounds of current game
                            currentGame.playrounds.forEach {
                                it.value.opponents.forEach {
                                    if (it.value.userID == user.userID) {
                                        user.score = score + it.value.answerScore
                                        score = user.score
                                    }
                                }
                            }

                            // put player with score into scoreboardArray
                            user.score = score
                            scoreboardArray.add(user)

                            // sort scoreboardArray descendingly and show list with players and scores
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


                // add extra scores to best three players
                users_here.forEach {
                    var current_id = it.key.userID
                    var old_score = it.value
                    val callbackUser = object :
                        Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            val user_updated = result
                            users_here.put(user_updated, user_updated.score - old_score);
                        }
                    }
                    DBMethods.getUserWithID(callbackUser, current_id)
                }


                for (x in 0 ..2){
                    val callbackUser = object :
                        Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            var user = result
                            if(x == 0){
                                user.score = user.score + gameWinnerScore
                            } else if (x == 1) {
                                user.score = user.score + gameSecondScore
                            } else {
                                user.score = user.score + gameThirdScore
                            }
                            editUser(user.userID, user)
                        }
                    }
                    DBMethods.getUserWithID(callbackUser, users_here.maxBy{ it.value }?.key?.userID.toString())
                    users_here.remove(users_here.maxBy{ it.value }?.key)
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

    /**
     * disable backButton on device
     */
    override fun onBackPressed() {
        println("do nothing")
    }
}