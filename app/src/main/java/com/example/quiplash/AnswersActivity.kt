package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameMethods.GameCalls.Companion.startTimer
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameManager.Companion.startSeconds
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

        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        setContentView(R.layout.activity_answers)
        timerView = findViewById(R.id.timer2A)
        val othertimer = findViewById<TextView>(R.id.timer3A)
        val questionTV = findViewById<TextView>(R.id.questionAnswer)
        val answerTV1 = findViewById<TextView>(R.id.answer1)
        val answerTV2 = findViewById<TextView>(R.id.answerRoundWinner)
        val roundTextView = findViewById<TextView>(R.id.roundsA)
        othertimer.visibility = View.INVISIBLE

        db.document(game.gameID).get()
            .addOnSuccessListener {
                Log.d("SUCCESS1", "DocumentSnapshot successfully deleted!")
                game = it.toObject(Game::class.java)!!
                questionTV.text = game.playrounds[game.activeRound - 1].question
                answerTV1.text = game.playrounds[game.activeRound - 1].opponents[0].answer
                answerTV2.text = game.playrounds[game.activeRound - 1].opponents[1].answer
                roundTextView.text = "${ceil(game.activeRound.toDouble()/3).toInt()}/${game.rounds}"
            }
            .addOnFailureListener { e -> Log.w("ERROR", "Error deleting document", e) }

        startTimer(timerView, startSeconds)


        Handler().postDelayed({
            val intent = Intent(this, EvaluationActivity::class.java)
            startActivity(intent)
        }, startSeconds*1000)

    }



    override fun onBackPressed() {
        println("do nothing")
    }

}