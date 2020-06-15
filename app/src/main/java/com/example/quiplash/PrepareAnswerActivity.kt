package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.getRandomQuestion
import com.google.firebase.firestore.CollectionReference
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameMethods.GameCalls.Companion.startTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrepareAnswerActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null

    var userindex = 0
    private lateinit var viewFlipper: ViewFlipper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        auth = FirebaseAuth.getInstance()

        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!

                userindex = game.playrounds[game.activeRound-1].opponents.indexOfFirst { it.userID == auth!!.currentUser?.uid }

            }
        setContentView(R.layout.activity_prepare_answer)

        val textViewTimer = findViewById<TextView>(R.id.timerView)
        val textViewRound = findViewById<TextView>(R.id.roundCounterView)
        val viewQuestion = findViewById<View>(R.id.viewQuestion)
        val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)
        val textViewQuestion2 = findViewById<TextView>(R.id.textViewQuestion2)
        val btnReady = findViewById<Button>(R.id.btnReady)
        val fieldAnswer = findViewById<EditText>(R.id.answerField)
        viewFlipper = findViewById(R.id.viewFlipperQuestion) // get the reference of ViewFlipper


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        viewFlipper.inAnimation = inAni
        viewFlipper.outAnimation = out

        //var question_count = game.rounds*game.users.count()/2

            startTimer(textViewTimer, 20)

            viewQuestion.setOnClickListener {
            viewFlipper.showNext()
        }

        btnReady.setOnClickListener {
            game.playrounds[game.activeRound-1].opponents[userindex].answer = fieldAnswer.text.toString()
            db.document("PFIoKme1vrCRnGpSsORn")
                .set(game)
                .addOnSuccessListener {
                    Log.d("Success", "DocumentSnapshot successfully written!")
                    val intent = Intent(this, AnswersActivity::class.java);
                    startActivity(intent);
                }
                .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }

        }
    }

    override fun onBackPressed() {
        println("do nothing")
    }
}