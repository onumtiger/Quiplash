package com.example.quiplash.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.startTimer
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.game.GameManager.Companion.startSecondsVoting
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.ceil


class AnswersActivity : AppCompatActivity() {

    lateinit var timerView : TextView

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sounds.playVotingSound(this)

        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_answers)
        timerView = findViewById(R.id.timer2A)
        val othertimer = findViewById<TextView>(R.id.timer3A)
        val questionTV = findViewById<TextView>(R.id.questionAnswer)
        val answerTV1 = findViewById<TextView>(R.id.answer1)
        val answerTV2 = findViewById<TextView>(R.id.answerRoundWinner)
        val roundTextView = findViewById<TextView>(R.id.roundsA)
        othertimer.visibility = View.INVISIBLE

        val callbackGame = object :
            Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                questionTV.text = game.playrounds.getValue("round${game.activeRound}").question
                answerTV1.text = game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                    GameManager.opp0).answer
                answerTV2.text = game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                    GameManager.opp1).answer
                roundTextView.text = "${ceil((game.activeRound+1).toDouble()/3).toInt()}/${game.rounds}"
            }
        }
        DBMethods.getCurrentGame(callbackGame,game.gameID)


        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                val intent = Intent(this@AnswersActivity, EvaluationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)


    }



    override fun onBackPressed() {
        println("do nothing")
    }

}