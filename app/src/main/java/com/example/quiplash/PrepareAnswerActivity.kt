package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameManager.Companion.startSecondsAnswer
import com.example.quiplash.GameMethods.Companion.startTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

class PrepareAnswerActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null

    var userindex = 0
    var answersArrived = false
    private lateinit var viewFlipper: ViewFlipper

    lateinit var awaitAllAnswers: ListenerRegistration

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({ Sounds.playAnswerSound(this)}, 3000)


        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        auth = FirebaseAuth.getInstance()

        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                userindex = if( game.playrounds.getValue("round${game.activeRound-1}").opponents.getValue("opponent0").userID == auth!!.currentUser?.uid) {
                    0
                } else{
                    1
                }

            }

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_prepare_answer)

        val textViewTimer = findViewById<TextView>(R.id.timerView)
        val textViewRound = findViewById<TextView>(R.id.roundCounterView)
        val viewQuestion = findViewById<View>(R.id.viewQuestion)
        val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)
        val textViewQuestion2 = findViewById<TextView>(R.id.textViewQuestion2)
        val btnReady = findViewById<Button>(R.id.btnReady)
        val fieldAnswer = findViewById<EditText>(R.id.answerField)
        val imageCheckmark = findViewById<ImageView>(R.id.imageCheckmark)
        val textAnswerState = findViewById<TextView>(R.id.textViewAnswerSaved)
        viewFlipper = findViewById(R.id.viewFlipperQuestion) // get the reference of ViewFlipper

        textViewRound.text = "${ceil(game.activeRound.toDouble() / 3).toInt()}/${game.rounds}"
        textViewQuestion.text = game.playrounds.getValue("round${game.activeRound-1}").question
        textViewQuestion2.text = game.playrounds.getValue("round${game.activeRound-1}").question

        textViewQuestion.text = game.questions.first().question
        //textViewQuestion2.text = game.questions.first().question
        //viewQuestion.text = game.questions.first().question

        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        viewFlipper.inAnimation = inAni
        viewFlipper.outAnimation = out

        //var question_count = game.rounds*game.users.count()/2

        val callbackTimer = object : Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                if(!answersArrived){
                    gotoAnswers()
                }
            }
        }
        startTimer(textViewTimer, startSecondsAnswer, callbackTimer)

        viewQuestion.setOnClickListener {
            Sounds.playClickSound(this)
            Sounds.playAnswerSound(this)

            viewFlipper.showNext()
        }

        btnReady.setOnClickListener {
            Sounds.playClickSound(this)

            db.document(game.gameID)
                .update(
                    mapOf(
                        "playrounds.round${game.activeRound - 1}.opponents.${getOpponentsIndex(auth!!.currentUser?.uid.toString())}.answer" to fieldAnswer.text.toString()
                    )
                )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                    imageCheckmark.visibility = ImageView.VISIBLE
                        textAnswerState.visibility = TextView.VISIBLE
                }
                .addOnFailureListener { e ->
                    Log.w("FAILURE", "Error updating document", e)
                    imageCheckmark.visibility = ImageView.INVISIBLE
                    textAnswerState.text = "Your Answer could not be saved"
                }


        }



        awaitAllAnswers = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("SUCCESS", "Current data: ${snapshot.data}")
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.getValue("round${game.activeRound-1}").opponents.getValue("opponent0").answer != "" && game.playrounds.getValue("round${game.activeRound-1}").opponents.getValue("opponent1").answer != "") {
                    gotoAnswers()
                }

            }
        }

    }

    private fun gotoAnswers() {
        awaitAllAnswers.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        answersArrived = true
        val intent = Intent(this, AnswersActivity::class.java)
        startActivity(intent)

    }

    private fun getOpponentsIndex(userid: String): String {
        var opponentkey = ""
        game.playrounds.getValue("round${game.activeRound - 1}").opponents.forEach {
            if (it.value.userID == userid) {
                opponentkey = it.key
                return@forEach
            }
            return@forEach
        }

        return opponentkey
    }

    override fun onBackPressed() {
        println("do nothing")
    }

}