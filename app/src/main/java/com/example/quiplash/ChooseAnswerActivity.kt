package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameManager.Companion.startSeconds
import com.example.quiplash.GameMethods.GameCalls.Companion.startTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

class ChooseAnswerActivity : AppCompatActivity() {

    lateinit var timerView: TextView
    lateinit var timerViewWaiting: TextView

    var START_MILLI_SECONDS = 60000L

    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false
    var time_in_milli_seconds = 0L

    private lateinit var simpleViewFlipper: ViewFlipper


    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"
    private var auth: FirebaseAuth? = null

    private lateinit var awaitAnswerChoosen: ListenerRegistration
    private var showAnswersFlag = false
    var chooseAnswerFlag = false


    @SuppressLint("WrongViewCast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_choose_answer)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        timerView = findViewById(R.id.timer2)
        timerViewWaiting = findViewById(R.id.timerWaiting2)
        val othertimer = findViewById<TextView>(R.id.timer3)
        val answerTV1 = findViewById<TextView>(R.id.answer1)
        val answerTV2 = findViewById<TextView>(R.id.answer2)
        val questionTV = findViewById<TextView>(R.id.questionCA)
        val answerView1 = findViewById<View>(R.id.view2)
        val answerView2 = findViewById<View>(R.id.view3)
        val roundView = findViewById<TextView>(R.id.roundsCA2)
        simpleViewFlipper =
            findViewById(R.id.simpleViewFlipperCA) // get the reference of ViewFlipper
        othertimer.visibility = View.INVISIBLE

        startTimer(timerViewWaiting, startSeconds)
        roundView.text = "${ceil(game.activeRound.toDouble()/3).toInt()} / ${game.rounds}"


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out

        answerView1.setOnClickListener {
            saveVote(0)
        }

        answerView2.setOnClickListener {
            saveVote(1)
        }



        awaitAnswerChoosen = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("SUCCESS", "Current data: ${snapshot.data}")
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds[game.activeRound - 1].opponents[0].answer != "" && game.playrounds[game.activeRound - 1].opponents[1].answer != "") {
                    awaitAnswerChoosen.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
                    showAnswersFlag = true
                    simpleViewFlipper.showNext()

                    db.document(game.gameID).get()
                        .addOnSuccessListener { documentSnapshot ->
                            game = documentSnapshot.toObject(Game::class.java)!!
                            questionTV.text = game.playrounds[game.activeRound - 1].question
                            answerTV1.text = game.playrounds[game.activeRound - 1].opponents[0].answer
                            answerTV2.text = game.playrounds[game.activeRound - 1].opponents[1].answer
                        }

                    startTimer(timerView, startSeconds)
                }

            }
        }

        Handler().postDelayed({
            if(!showAnswersFlag) {
                simpleViewFlipper.showNext()
            }
        }, START_MILLI_SECONDS )

        Handler().postDelayed({
            if(!chooseAnswerFlag) {
                val intent = Intent(this, EvaluationActivity::class.java)
                startActivity(intent)
            }
        }, START_MILLI_SECONDS * 2)
    }

    override fun onBackPressed() {
        println("do nothing")
    }


    private fun getVotersIndex(userid: String): Int {
        return game.playrounds[game.activeRound - 1].voters.indexOf(Voter(userid))
    }

    fun saveVote(answerIndex: Int) {
        game.playrounds[game.activeRound - 1].voters[getVotersIndex(auth!!.currentUser?.uid.toString()) + 1].voteUserID =
            game.playrounds[game.activeRound - 1].opponents[answerIndex].userID.toString()
        db.document(game.gameID)
            .set(game)
            .addOnSuccessListener {
                Log.d("Success", "DocumentSnapshot successfully written!")
                chooseAnswerFlag = true
                val intent = Intent(this, EvaluationActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
    }
}