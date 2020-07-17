package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.BounceInterpolator
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.game.GameManager.Companion.startSecondsAnswer
import com.example.quiplash.game.GameManager.Companion.startTimer
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ceil

/**
 * This View is for Players who have to Answer the Question for the current round.
 * A View-Flipper provides the Question for this round and a Textfield to enter the Answer.
 * **/
class PrepareAnswerActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null

    private var userindex = 0
    var answersArrived = false

    //View
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var imageCheckmark: ImageView
    private lateinit var fieldAnswer: EditText
    private lateinit var textAnswerState: TextView
    private lateinit var layoutAnswerSaved: FrameLayout

    private lateinit var awaitAllAnswers: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            Sounds.playAnswerSound(
                this
            )
        }, 3000)


        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        auth = FirebaseAuth.getInstance()

        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                userindex =
                    if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                            GameManager.opp0
                        ).userID == auth!!.currentUser?.uid
                    ) {
                        0
                    } else {
                        1
                    }

            }

        //Remove Top_Bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_prepare_answer)

        //Set View-Elements
        val textViewTimer = findViewById<TextView>(R.id.timerView)
        val textViewRound = findViewById<TextView>(R.id.roundCounterView)
        val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)
        val textViewQuestion2 = findViewById<TextView>(R.id.textViewQuestion2)
        textAnswerState = findViewById(R.id.textViewAnswerSaved)
        val btnReady = findViewById<Button>(R.id.btnReady)
        fieldAnswer = findViewById(R.id.answerField)
        imageCheckmark = findViewById(R.id.imageCheckmark)
        layoutAnswerSaved = findViewById(R.id.layoutAnswerSaved)
        viewFlipper = findViewById(R.id.viewFlipperQuestion) // get the reference of ViewFlipper

        //Create Animation
        val splashanim: Animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val interpolator = BounceInterpolator(0.5, 10.0)
        splashanim.interpolator = interpolator
        textViewQuestion.startAnimation(splashanim)

        //Display Game-Infos in View
        textViewRound.text =
            ("${ceil((game.activeRound + 1).toDouble() / 3).toInt()}/${game.rounds}")
        textViewQuestion.text = game.playrounds.getValue("round${game.activeRound}").question
        textViewQuestion2.text = game.playrounds.getValue("round${game.activeRound}").question

        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        viewFlipper.inAnimation = inAni
        viewFlipper.outAnimation = out


        //Start Timer. If Timer ends and View hasn't already switched, go to next View.
        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                if (!answersArrived) {
                    gotoAnswers()
                }
            }
        }
        startTimer(textViewTimer, startSecondsAnswer, callbackTimer)

        //The first View displays  the Question. If Tapped, the next View will be shown
        textViewQuestion.setOnClickListener {
            Sounds.playClickSound(this)
            Sounds.playAnswerSound(this)
            viewFlipper.showNext()
        }

        //If Press Enter in TextField, call same Function as if 'submit'-button is pressed.
        fieldAnswer.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                saveAnswer()
                return@OnKeyListener true
            }
            false
        })

        //If Submit-button is pressed, save Answer.
        btnReady.setOnClickListener {
            Sounds.playClickSound(this)
            saveAnswer()
        }



        //Listen for changes in Database regarding the current Game. If both Answers are available, go to next View.
        awaitAllAnswers = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("SUCCESS", "Current data: ${snapshot.data}")
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0
                    ).answer != "" && game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp1
                    ).answer != "" && !answersArrived
                ) {
                    gotoAnswers()
                }

            }
        }

    }

    /**Send Player to next View**/
    private fun gotoAnswers() {
        awaitAllAnswers.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        answersArrived = true
        val intent = Intent(this, AnswersActivity::class.java)
        startActivity(intent)

    }

    /**Save Answer into Database and give visual Feddback, when saved successfully or if fails.**/
    private fun saveAnswer(){
        db.document(game.gameID)
            .update(
                mapOf(
                    "playrounds.round${game.activeRound}.opponents.${getOpponentsIndex(auth!!.currentUser?.uid.toString())}.answer" to fieldAnswer.text.toString()
                )
            )
            .addOnSuccessListener {
                Log.d("SUCCESS", "DocumentSnapshot successfully updated!")
                imageCheckmark.visibility = ImageView.VISIBLE
                textAnswerState.visibility = TextView.VISIBLE
                val splashanim: Animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                val answerInterpolator = BounceInterpolator(0.5, 10.0)
                splashanim.interpolator = answerInterpolator
                layoutAnswerSaved.startAnimation(splashanim)
            }
            .addOnFailureListener { e ->
                Log.w("FAILURE", "Error updating document", e)
                imageCheckmark.visibility = ImageView.INVISIBLE
                textAnswerState.text = getString(R.string.answer_not_saved)
            }
    }

    /**For saving the Answer to corresponding 'Opponent' in database we need to
     * know wether the player is 'opponent 0' or 'opponent 1'**/
    private fun getOpponentsIndex(userid: String): String {
        var opponentkey = ""
        game.playrounds.getValue("round${game.activeRound}").opponents.forEach {
            if (it.value.userID == userid) {
                opponentkey = it.key
                return@forEach
            }
            return@forEach
        }

        return opponentkey
    }


    //Disable Back-Btn on Device
    override fun onBackPressed() {
        println("do nothing")
    }

}