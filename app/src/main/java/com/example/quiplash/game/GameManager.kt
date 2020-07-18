package com.example.quiplash.game

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import com.example.quiplash.LandingActivity
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.user.UserQP
import com.google.firebase.firestore.FirebaseFirestore

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class GameManager {

    companion object {
        var user = UserQP()
        var game = Game()
        const val startSecondsAnswer = 90L
        const val startSecondsVoting = 15L
        const val startSecondsIdle = 60L
        lateinit var countdownTimer: CountDownTimer
        var time_in_milli_seconds = 0L
        const val voteScore = 10
        const val roundWinnerScore = 50
        const val gameWinnerScore = 100
        const val gameSecondScore = 70
        const val gameThirdScore = 50
        const val opp0 = "opponent0"
        const val opp1 = "opponent1"


        // setter
        fun setUserinfo(userinfo: UserQP) {
            user = userinfo
        }

        //Path to Games in Database
        val db = FirebaseFirestore.getInstance().collection(DBMethods.gamesPath)

        // gametimer start
        fun startTimer(textView: TextView, time_in_seconds: Long, callback: Callback<Boolean>) {
            countdownTimer = object : CountDownTimer(time_in_seconds * 1000, 1000) {
                override fun onFinish() {
                    callback.onTaskComplete(true)
                }
                override fun onTick(p0: Long) {
                    time_in_milli_seconds = p0
                    updateTextUI(
                        textView
                    )
                }
            }
            countdownTimer.start()
        }

        //Upating UI of Timer
        private fun updateTextUI(textView: TextView) {
            val minute = (time_in_milli_seconds / 1000) / 60
            val seconds = (time_in_milli_seconds / 1000) % 60
            if (seconds < 10) {
                textView.text = ("$minute:0$seconds")
            } else {
                textView.text = ("$minute:$seconds")
            }
        }

        //Timer pause
        fun pauseTimer() {
            countdownTimer.cancel()
        }

        /**
         * This Function examines wether a player is a 'Voter' or one of the 'Opponents' (Those who have to answer a question).
         * Then send Player to corresponding View:
         * Voter -> 'Choose Answer' (Wait for all answers, than vot for the best answer)
         * Opponent -> 'Prepare Answer' (Watch Question, then answer the question)
         * **/
        fun playerAllocation(appcontext: Context, userid: String) {
            if (game.playrounds.size >= game.activeRound) {

                if (game.playrounds.getValue("round${game.activeRound}").voters.contains(userid)
                ) {
                    val intent = Intent(appcontext, ChooseAnswerActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    appcontext.startActivity(intent)

                } else {
                    val intent = Intent(appcontext, PrepareAnswerActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    appcontext.startActivity(intent)
                }

            } else {
                val intent = Intent(
                    appcontext,
                    LandingActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appcontext.startActivity(intent)
            }
        }


        /**
         * Set additional Scores for the Winner of this Round.
         * Save new Scoring into Database.
         * First fetch game-info from Database. Then check wether additional scroing hav elaredy been added by other users (players).
         * If not examine the winner of this round and save bonus-points tho the winners' answer.
         * **/
        fun setPoints(callback: Callback<Boolean>) {
            val callbackGame = object :
                Callback<Game> {
                override fun onTaskComplete(result: Game) {
                    game = result

                    //First check wether Round-scores have already been updated by other users/players.
                    if ((game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                            opp0
                        ).answerScore + game.playrounds.getValue(
                            "round${game.activeRound}"
                        ).opponents.getValue(opp1).answerScore) > (game.playrounds.getValue("round${game.activeRound}").voters.size * voteScore)
                    ) {
                        callback.onTaskComplete(true)
                    } else{ //If scores have not been updated yet, update scoring

                        if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                                opp0
                            ).answerScore > game.playrounds.getValue(
                                "round${game.activeRound}"
                            ).opponents.getValue(opp1).answerScore
                        ) {
                            game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                                opp0
                            ).answerScore += roundWinnerScore
                        } else if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                                opp0
                            ).answerScore < game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                                opp1
                            ).answerScore
                        ) {
                            game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                                opp1
                            ).answerScore += roundWinnerScore
                        }

                    db.document(game.gameID)
                        .update(
                            mapOf(
                                "playrounds.round${game.activeRound}.opponents.opponent0.answerScore" to game.playrounds.getValue(
                                    "round${game.activeRound}"
                                ).opponents.getValue(opp0).answerScore,
                                "playrounds.round${game.activeRound}.opponents.opponent1.answerScore" to game.playrounds.getValue(
                                    "round${game.activeRound}"
                                ).opponents.getValue(opp1).answerScore
                            )
                        )
                        .addOnSuccessListener {
                            callback.onTaskComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "FAILURE",
                                "Error updating document",
                                e
                            )
                        }
                }
                }
            }
            DBMethods.getCurrentGame(
                callbackGame,
                game.gameID
            )

        }


    }

}