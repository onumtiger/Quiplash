package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.BounceInterpolator
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.game.GameManager.Companion.startSecondsVoting
import com.example.quiplash.game.GameManager.Companion.startSecondsAnswer
import com.example.quiplash.game.GameMethods.Companion.startTimer
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

class ChooseAnswerActivity : AppCompatActivity() {

    lateinit var timerView: TextView
    private lateinit var timerViewWaiting: TextView

    private lateinit var simpleViewFlipper: ViewFlipper


    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath
    private var auth: FirebaseAuth? = null

    private lateinit var awaitAnswerChoosen: ListenerRegistration
    private var answersArrived = false
    private var answerChoosen = 2

    private lateinit var answerView1 : View
    private lateinit var answerView2 : View
    private lateinit var answerTV1 : TextView
    private lateinit var answerTV2 : TextView
    private lateinit var questionTV : TextView
    private lateinit var imageCheckA1 : ImageView
    private lateinit var imageCheckA2 : ImageView

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
        //val othertimer = findViewById<TextView>(R.id.timer3)
        answerTV1 = findViewById(R.id.answer1)
        answerTV2 = findViewById(R.id.answer2)
        questionTV = findViewById(R.id.questionCA)
        answerView1 = findViewById(R.id.view2)
        answerView2 = findViewById(R.id.view3)
        val roundView = findViewById<TextView>(R.id.roundsCA2)
        imageCheckA1 = findViewById(R.id.imageCheckAnswer1)
        imageCheckA2 = findViewById(R.id.imageCheckAnswer2)
        simpleViewFlipper =
            findViewById(R.id.simpleViewFlipperCA) // get the reference of ViewFlipper
        //othertimer.visibility = View.INVISIBLE

        val callbackTimerWaiting = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                if(!answersArrived){
                    setNextView()
                }
            }
        }
        startTimer(timerViewWaiting, startSecondsAnswer, callbackTimerWaiting)

        roundView.text = ("${ceil((game.activeRound+1).toDouble()/3).toInt()}/${game.rounds}")


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out

        answerView1.setOnClickListener {
                saveVote(0)

            Sounds.playClickSound(this)
            val splashanim = AnimationUtils.loadAnimation(this, R.anim.little_shake)
            val interpolator = BounceInterpolator(0.5, 5.0)
            splashanim.interpolator = interpolator
            answerView1.startAnimation(splashanim)
        }

        answerView2.setOnClickListener {
                saveVote(1)

            Sounds.playClickSound(this)
            val splashanim = AnimationUtils.loadAnimation(this, R.anim.little_shake)
            val interpolator = BounceInterpolator(0.5, 5.0)
            splashanim.interpolator = interpolator
            answerView2.startAnimation(splashanim)
        }



        awaitAnswerChoosen = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameMethods.opp0).answer != "" && game.playrounds.getValue(
                        "round${game.activeRound}"
                    ).opponents.getValue(GameMethods.opp1).answer != ""
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

        val callbackGame = object :
            Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                questionTV.text =
                    game.playrounds.getValue("round${game.activeRound}").question
                answerTV1.text =
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameMethods.opp0
                    ).answer
                answerTV2.text =
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameMethods.opp1
                    ).answer
            }
        }
        DBMethods.getCurrentGame(callbackGame, game.gameID)

        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                val intent =
                    Intent(this@ChooseAnswerActivity, EvaluationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)
    }

    private fun getVotersIndex(userid: String): String {
        var voterkey = ""
        game.playrounds.getValue("round${game.activeRound}").voters.forEach {
            if (it.value.userID == userid) {
                voterkey = it.key
                return@forEach
            }
            return@forEach
        }

        return voterkey
    }

    private fun saveVote(answerIndex: Int) {
        //if already votet
        if (answerChoosen<=1){

            db.document(game.gameID)
                .update(
                    mapOf(
                        "playrounds.round${game.activeRound}.opponents.opponent$answerChoosen.answerScore" to FieldValue.increment(
                            - GameMethods.voteScore.toDouble()
                        ),
                        "playrounds.round${game.activeRound}.opponents.opponent$answerIndex.answerScore" to FieldValue.increment(
                            GameMethods.voteScore.toDouble()
                        )
                    )
                )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                    answerChoosen = answerIndex
                    if(answerIndex == 0){
                        imageCheckA1.visibility = View.VISIBLE
                        imageCheckA2.visibility = View.INVISIBLE
                        val splashanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                        val interpolator = BounceInterpolator(0.2, 10.0)
                        splashanim.interpolator = interpolator
                        imageCheckA1.startAnimation(splashanim)
                    } else if(answerIndex == 1){
                        imageCheckA2.visibility = View.VISIBLE
                        imageCheckA1.visibility = View.INVISIBLE
                        val splashanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                        val interpolator = BounceInterpolator(0.2, 10.0)
                        splashanim.interpolator = interpolator
                        imageCheckA2.startAnimation(splashanim)
                    }
                }
                .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
        } else {
            answerChoosen = answerIndex
            game.playrounds.getValue("round${game.activeRound}").opponents.getValue(GameMethods.opp0).answer

            db.document(game.gameID)
                .update(
                    mapOf(
                        /*"playrounds.round${game.activeRound}.voters.${getVotersIndex(auth!!.currentUser?.uid.toString())}.voteUserID" to game.playrounds.getValue(
                            "round${game.activeRound}"
                        ).opponents.getValue("opponent$answerIndex").userID,*/
                        "playrounds.round${game.activeRound}.opponents.opponent$answerIndex.answerScore" to FieldValue.increment(
                            GameMethods.voteScore.toDouble()
                        )
                    )
                )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                    if(answerIndex == 0){
                        imageCheckA1.visibility = View.VISIBLE
                    } else if(answerIndex == 1){
                        imageCheckA2.visibility = View.VISIBLE

                    }
                }
                .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
        }




    }
}