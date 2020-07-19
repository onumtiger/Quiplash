package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
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

/**
 * This View is for Players who have to Answer the Question for the current round.
 * After the player has answered the question this View will be shown next.
 * It Shows the Answers of both Players / Opponents who had to answer the question,
 * while the other Players are Voting for one Answer.
 * **/
class AnswersActivity : AppCompatActivity() {

    //View
    lateinit var timerView : TextView

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sounds.playVotingSound(this)

        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        setContentView(R.layout.activity_answers)

        //Set View-Elements
        timerView = findViewById(R.id.timer2A)
        val questionTV = findViewById<TextView>(R.id.questionAnswer)
        val answerTV1 = findViewById<TextView>(R.id.answer1)
        val answerTV2 = findViewById<TextView>(R.id.answer2)
        val roundTextView = findViewById<TextView>(R.id.roundsA)

        //Get Game-Object and set Infos into View
        val callbackGame = object :
            Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                questionTV.text = game.playrounds.getValue("round${game.activeRound}").question
                answerTV1.text = game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                    GameManager.opp0).answer
                answerTV2.text = game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                    GameManager.opp1).answer
                roundTextView.text = ("${ceil((game.activeRound+1).toDouble()/(game.playrounds.size/game.rounds)).toInt()}/${game.rounds}")
            }
        }
        DBMethods.getCurrentGame(callbackGame,game.gameID)

        //Setup and Start Timer
        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                //If Timer ends go to Next View -> Evaluation
                val intent = Intent(this@AnswersActivity, EvaluationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)


    }


    //Disable Back-Btn on Device
    override fun onBackPressed() {
        println("do nothing")
    }

}