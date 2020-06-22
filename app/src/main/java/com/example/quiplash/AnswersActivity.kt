package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameMethods.Companion.startTimer
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameManager.Companion.startSecondsVoting
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.ceil


class AnswersActivity : AppCompatActivity() {

    lateinit var timerView : TextView

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"


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

        val callbackGame = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                questionTV.text = game.playrounds.getValue("round${game.activeRound-1}").question
                answerTV1.text = game.playrounds.getValue("round${game.activeRound-1}").opponents.getValue("opponent0").answer
                answerTV2.text = game.playrounds.getValue("round${game.activeRound-1}").opponents.getValue("opponent1").answer
                roundTextView.text = "${ceil(game.activeRound.toDouble()/3).toInt()}/${game.rounds}"
            }
        }
        DBMethods.DBCalls.getCurrentGame(callbackGame,game.gameID)


        val callbackTimer = object : Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                val intent = Intent(this@AnswersActivity, EvaluationActivity::class.java)
                startActivity(intent)
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)


    }



    override fun onBackPressed() {
        println("do nothing")
    }

}