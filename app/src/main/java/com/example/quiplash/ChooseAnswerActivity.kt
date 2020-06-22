package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameManager.Companion.game
import com.example.quiplash.GameManager.Companion.startSecondsVoting
import com.example.quiplash.GameManager.Companion.startSecondsAnswer
import com.example.quiplash.GameMethods.Companion.startTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

class ChooseAnswerActivity : AppCompatActivity() {

    lateinit var timerView: TextView
    lateinit var timerViewWaiting: TextView

    //var START_MILLI_SECONDS = 60000L

    //lateinit var countdown_timer: CountDownTimer
    //var isRunning: Boolean = false
    //var time_in_milli_seconds = 0L

    private lateinit var simpleViewFlipper: ViewFlipper


    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"
    private var auth: FirebaseAuth? = null

    private lateinit var awaitAnswerChoosen: ListenerRegistration
    private var answersArrived = false
    private var answerChoosen = false

    private lateinit var answerView1 : View
    private lateinit var answerView2 : View
    private lateinit var answerTV1 : TextView
    private lateinit var answerTV2 : TextView
    private lateinit var questionTV : TextView

    @SuppressLint("WrongViewCast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_choose_answer)
        Sounds.playVotingSound(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        timerView = findViewById(R.id.timer2)
        timerViewWaiting = findViewById(R.id.timerWaiting2)
        val othertimer = findViewById<TextView>(R.id.timer3)
        answerTV1 = findViewById<TextView>(R.id.answer1)
        answerTV2 = findViewById<TextView>(R.id.answer2)
        questionTV = findViewById<TextView>(R.id.questionCA)
        answerView1 = findViewById(R.id.view2)
        answerView2 = findViewById(R.id.view3)
        val roundView = findViewById<TextView>(R.id.roundsCA2)
        simpleViewFlipper =
            findViewById(R.id.simpleViewFlipperCA) // get the reference of ViewFlipper
        othertimer.visibility = View.INVISIBLE

        val callbackTimerWaiting = object : Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                if(!answersArrived){
                    setNextView()
                }
            }
        }
        startTimer(timerViewWaiting, startSecondsAnswer, callbackTimerWaiting)

        roundView.text = "${ceil(game.activeRound.toDouble() / 3).toInt()} / ${game.rounds}"


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out

        answerView1.setOnClickListener {
            if(!answerChoosen) {
                saveVote(0)
            }
            Sounds.playClickSound(this)
        }

        answerView2.setOnClickListener {
            if(!answerChoosen) {
                saveVote(1)
            }
            Sounds.playClickSound(this)
        }



        awaitAnswerChoosen = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.getValue("round${game.activeRound - 1}").opponents.getValue("opponent0").answer != "" && game.playrounds.getValue(
                        "round${game.activeRound - 1}"
                    ).opponents.getValue("opponent1").answer != ""
                ) {
                    if(!answersArrived){
                        setNextView()
                    }
                }

            }
        }

    }

    override fun onBackPressed() {
        println("do nothing")
    }

    private fun setNextView(){
        awaitAnswerChoosen.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        answersArrived = true
        simpleViewFlipper.showNext()

        val callbackGame = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                questionTV.text =
                    game.playrounds.getValue("round${game.activeRound - 1}").question
                answerTV1.text =
                    game.playrounds.getValue("round${game.activeRound - 1}").opponents.getValue(
                        "opponent0"
                    ).answer
                answerTV2.text =
                    game.playrounds.getValue("round${game.activeRound - 1}").opponents.getValue(
                        "opponent1"
                    ).answer
            }
        }
        DBMethods.DBCalls.getCurrentGame(callbackGame, game.gameID)

        val callbackTimer = object : Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                val intent =
                    Intent(this@ChooseAnswerActivity, EvaluationActivity::class.java)
                startActivity(intent)
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)
    }

    private fun getVotersIndex(userid: String): String {
        var voterkey = ""
        game.playrounds.getValue("round${game.activeRound - 1}").voters.forEach {
            if (it.value.userID == userid) {
                voterkey = it.key
                return@forEach
            }
            return@forEach
        }

        return voterkey
    }

    private fun saveVote(answerIndex: Int) {
        answerChoosen = true
        game.playrounds.getValue("round${game.activeRound - 1}").opponents.getValue("opponent0").answer

        db.document(game.gameID)
            .update(
                mapOf(
                    "playrounds.round${game.activeRound - 1}.voters.${getVotersIndex(auth!!.currentUser?.uid.toString())}.voteUserID" to game.playrounds.getValue(
                        "round${game.activeRound - 1}"
                    ).opponents.getValue("opponent$answerIndex").userID,
                    "playrounds.round${game.activeRound - 1}.opponents.opponent$answerIndex.answerScore" to FieldValue.increment(
                        10
                    )
                )
            )
            .addOnSuccessListener { Log.d("SUCCESS", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }


    }
}