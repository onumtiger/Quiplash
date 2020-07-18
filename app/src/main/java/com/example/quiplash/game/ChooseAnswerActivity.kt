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
import com.example.quiplash.game.GameManager.Companion.startTimer
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

/**
 * This View is for Players who have to Vote for Answer for the current round.
 * While the Players who have to answer the question, the Voters are in a 'waiting-view'.
 * Afterwards the Voters can vote for an answer which will be saved.
 * **/
class ChooseAnswerActivity : AppCompatActivity() {

    //View
    lateinit var timerView: TextView
    private lateinit var timerViewWaiting: TextView
    private lateinit var simpleViewFlipper: ViewFlipper
    private lateinit var answerView1: View
    private lateinit var answerView2: View
    private lateinit var answerTV1: TextView
    private lateinit var answerTV2: TextView
    private lateinit var questionTV: TextView
    private lateinit var imageCheckA1: ImageView
    private lateinit var imageCheckA2: ImageView

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath
    private var auth: FirebaseAuth? = null

    //Variables
    private lateinit var awaitAnswerChoosen: ListenerRegistration
    private var answersArrived = false
    private var answerChoosen = 2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_answer)

        //Start Sound
        Sounds.playVotingSound(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        //Set View-Elements
        timerView = findViewById(R.id.timer2)
        timerViewWaiting = findViewById(R.id.timerWaiting2)
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
        roundView.text = ("${ceil((game.activeRound + 1).toDouble() / game.rounds).toInt()}/${game.rounds}")

        //Setup and Start Timer
        val callbackTimerWaiting = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                //If Timer ends and the View hasn't already switched...
                if (!answersArrived) {
                    setNextView() //then go to next View
                }
            }
        }
        startTimer(timerViewWaiting, startSecondsAnswer, callbackTimerWaiting)


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out

        //Click on Answer (1)
        answerView1.setOnClickListener {
            saveVote(0) // save Vote
            Sounds.playClickSound(this) // play Sound
            //Start animation of Answer-View
            val splashanim = AnimationUtils.loadAnimation(this, R.anim.little_shake)
            val interpolator = BounceInterpolator(0.5, 5.0)
            splashanim.interpolator = interpolator
            answerView1.startAnimation(splashanim)
        }

        //Click on Answer (2)
        answerView2.setOnClickListener {
            saveVote(1) // save Vote
            Sounds.playClickSound(this) // play Sound
            //Start animation of Answer-View
            val splashanim = AnimationUtils.loadAnimation(this, R.anim.little_shake)
            val interpolator = BounceInterpolator(0.5, 5.0)
            splashanim.interpolator = interpolator
            answerView2.startAnimation(splashanim)
        }


        //Listen to changes of Database. Await if Data of Game changes.
        awaitAnswerChoosen = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                //If Both Answers are available & The View hasn't already switched (due to expired Time), go to next View -> Show Answers
                if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0
                    ).answer != "" && game.playrounds.getValue(
                        "round${game.activeRound}"
                    ).opponents.getValue(GameManager.opp1).answer != ""
                ) {
                    if (!answersArrived) {
                        setNextView()
                    }
                }

            }
        }

    }

    //Disable Back-Btn on Device
    override fun onBackPressed() {
        println("do nothing")
    }

    /**Setup and Switch to Next View: get Game-Infos and display in View. Start Timer for Voting**/
    private fun setNextView() {
        awaitAnswerChoosen.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        answersArrived = true
        simpleViewFlipper.showNext() // Switch to next View

        //Get Game-Info from Database
        val callbackGame = object :
            Callback<Game> {
            override fun onTaskComplete(result: Game) {
                //Display Game-Infos in View
                game = result
                questionTV.text =
                    game.playrounds.getValue("round${game.activeRound}").question
                answerTV1.text =
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0
                    ).answer
                answerTV2.text =
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp1
                    ).answer
            }
        }
        DBMethods.getCurrentGame(callbackGame, game.gameID)

        //Setup and Start Timer
        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                //If Timer ends go to next View
                val intent =
                    Intent(this@ChooseAnswerActivity, EvaluationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        startTimer(timerView, startSecondsVoting, callbackTimer)
    }

    /**
     * Save Vote in Database respectively Save Score for Vote Into Database.
     * Voter can Change ist vote during the given Time.
     * **/
    private fun saveVote(answerIndex: Int) {
        //if already votet -> Change Vote. Add Score to Choosen Answer and Remove Vote-Score from previous choosen answer.
        if (answerChoosen <= 1) {
            db.document(game.gameID)
                .update(
                    mapOf(
                        "playrounds.round${game.activeRound}.opponents.opponent$answerChoosen.answerScore" to FieldValue.increment(
                            -GameManager.voteScore.toDouble()
                        ),
                        "playrounds.round${game.activeRound}.opponents.opponent$answerIndex.answerScore" to FieldValue.increment(
                            GameManager.voteScore.toDouble()
                        )
                    )
                )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                    answerChoosen = answerIndex
                    if (answerIndex == 0) {
                        imageCheckA1.visibility = View.VISIBLE
                        imageCheckA2.visibility = View.INVISIBLE
                        val splashanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                        val interpolator = BounceInterpolator(0.2, 10.0)
                        splashanim.interpolator = interpolator
                        imageCheckA1.startAnimation(splashanim)
                    } else if (answerIndex == 1) {
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
            game.playrounds.getValue("round${game.activeRound}").opponents.getValue(GameManager.opp0).answer

            db.document(game.gameID)
                .update(
                    mapOf(
                        "playrounds.round${game.activeRound}.opponents.opponent$answerIndex.answerScore" to FieldValue.increment(
                            GameManager.voteScore.toDouble()
                        )
                    )
                )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                    if (answerIndex == 0) {
                        imageCheckA1.visibility = View.VISIBLE
                    } else if (answerIndex == 1) {
                        imageCheckA2.visibility = View.VISIBLE

                    }
                }
                .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
        }


    }
}