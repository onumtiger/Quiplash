package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.quiplash.BounceInterpolator
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.game.GameManager.Companion.startSecondsIdle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.ceil
import com.example.quiplash.game.GameManager.Companion.startTimer
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP

class EvaluationActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private lateinit var dbUsers: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath
    private val dbUsersPath = DBMethods.usersPath
    private var auth: FirebaseAuth? = null

    //Views
    private var answerViewWinner: TextView? = null
    private var winnerName: TextView? = null
    private var imageWinnerPhoto: ImageView? = null
    private var scoreView: TextView? = null
    private var frameProfile: RelativeLayout? = null
    private var imageWinnerSign: ImageView? = null

    private var imageAndIcon: TextView? = null
    private var answerViewWinnerDraw: TextView? = null
    private var winnerNameDraw: TextView? = null
    private var imageWinnerPhotoDraw: ImageView? = null
    private var imageLoserSign: ImageView? = null
    private var imageShot: TextView? = null
    private var scoreViewDraw: TextView? = null
    private var answerViewWinnerFrameDraw: View? = null
    private var frameProfileDraw: RelativeLayout? = null
    private var drinkView: TextView? = null

    private lateinit var awaitNextRound: ListenerRegistration
    private var nextroundFlag = false
    private var setRoundFlag = false
    private var oldRound = 0
    private var completeLayout: ConstraintLayout? = null

    //drink mode
    private var winnerNames = ""
    private var deuce = false
    private var secondName = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sounds.playScoreSound(this)
        setContentView(R.layout.activity_evaluation)

        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        dbUsers = FirebaseFirestore.getInstance().collection(dbUsersPath)
        auth = FirebaseAuth.getInstance()

        drinkView = findViewById(R.id.party_shot_text_view)
        completeLayout = findViewById(R.id.complete_layout)

        if (!game.partyMode){
            drinkView?.visibility = View.INVISIBLE
        } else {
            completeLayout?.setBackgroundResource(R.drawable.prty)
            val drnk = (0 until game.drinks.size).random()
            drinkView?.text = ("The Loosers challenge: \n" + game.drinks[drnk])
        }


        /**
         * Set Points for Round-Winner.
         * As soon as points are set, display Winner.
         * */
        val callbackPoints = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                if(result){
                    getWinner()
                }
            }
        }
        GameManager.setPoints(callbackPoints)

        //Remove Top-Bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


        val questionEval = findViewById<TextView>(R.id.questionEval)
        val textViewTimer = findViewById<TextView>(R.id.timerViewEval)
        answerViewWinner = findViewById(R.id.answerRoundWinner)
        winnerName = findViewById(R.id.textRoundWinnerName)
        imageWinnerPhoto = findViewById(R.id.imageRoundWinnerPhoto)
        scoreView = findViewById(R.id.textViewScore)
        frameProfile = findViewById(R.id.winner)
        imageWinnerSign = findViewById(R.id.imageWinnerSign)

        imageAndIcon = findViewById(R.id.textViewAnd)

        answerViewWinnerDraw = findViewById(R.id.answerRoundWinnerDraw)
        winnerNameDraw = findViewById(R.id.textRoundWinnerNameDraw)
        answerViewWinnerFrameDraw = findViewById(R.id.viewDraw)
        imageWinnerPhotoDraw = findViewById(R.id.imageRoundWinnerPhotoDraw)
        val roundViewEval = findViewById<TextView>(R.id.roundsEval)
        scoreViewDraw = findViewById(R.id.textViewScoreDraw)
        frameProfileDraw = findViewById(R.id.winnerDraw)
        imageLoserSign = findViewById(R.id.imageLoserSign)
        imageShot = findViewById(R.id.textViewShot)

        val nextBtn = findViewById<TextView>(R.id.buttonNext)

        //save current active-round
        oldRound = game.activeRound

        questionEval.text = game.playrounds.getValue("round${game.activeRound}").question
        roundViewEval.text = ("${ceil((game.activeRound+1).toDouble()/3).toInt()}/${game.rounds}")


        //Create Timer
        val callbackTimer = object :
            Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                Sounds.playClickSound(this@EvaluationActivity)
                if(!setRoundFlag) {
                    setRoundFlag = true
                    setNextRound()
                }
            }
        }
        startTimer(textViewTimer, startSecondsIdle, callbackTimer)

        //If last round change Text of button ('next round' -> 'show scoreboard')
        if (game.activeRound+1 == game.playrounds.size) {
            nextBtn.text = getString(R.string.show_scoreboard)
        }

        nextBtn.setOnClickListener {
            Sounds.playClickSound(this)
            if(!setRoundFlag) {
                setRoundFlag = true
                setNextRound()
            }
        }


        /**
         * If someone press the 'next round'-Button, go to next round.
         * Following Listener wait if the data of the game in the database changes.
         * If the value of 'activeRound' changed, go to next round.
         * **/
        awaitNextRound = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.activeRound+1 > oldRound+1) { //is activeRound new?
                    if (!nextroundFlag) {
                        gotoNextRound()
                    }

                }

            }
        }


    }


    /**
     * Imcrement 'active round' in database
     * **/
    private fun setNextRound() {
        db.document(game.gameID)
            .update("activeRound", oldRound + 1)
            .addOnSuccessListener { Log.d("SUCCESS", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }

    }

    /**
     * If active-round is the last round, go to 'scoreboard'.
     * Else go to next round
     * **/
    private fun gotoNextRound() {
        awaitNextRound.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        nextroundFlag = true
        GameManager.pauseTimer()

        if ((oldRound+1) < game.playrounds.size) {
            //Check which View comes next (Answer Question, Choose Answer)
            GameManager.playerAllocation(
                this.applicationContext,
                auth!!.currentUser?.uid.toString()
            )
            finish()
        } else {
            val intent = Intent(this, EndOfGameActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        println("do nothing")
    }


    /**
     * Get Scoring and select winner.
     * **/
    private fun getWinner() {
        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                when {
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0).answerScore > game.playrounds.getValue(
                        "round${game.activeRound}"
                    ).opponents.getValue(GameManager.opp1).answerScore
                    -> {
                        setWinnerInfo(
                            0,
                            frameProfile,
                            answerViewWinner,
                            scoreView,
                            winnerName,
                            imageWinnerPhoto
                        )

                        setWinnerInfo(
                            1,
                            frameProfileDraw,
                            answerViewWinnerDraw,
                            scoreViewDraw,
                            winnerNameDraw,
                            imageWinnerPhotoDraw
                        )

                        val zoomanim = AnimationUtils.loadAnimation(this, R.anim.zoom)
                        imageWinnerSign!!.visibility = ImageView.VISIBLE
                        imageWinnerSign!!.startAnimation(zoomanim)
                        imageLoserSign!!.visibility = ImageView.VISIBLE
                        imageLoserSign!!.startAnimation(zoomanim)

                    }
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0
                    ).answerScore < game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp1
                    ).answerScore
                    -> {
                        setWinnerInfo(
                            1,
                            frameProfile,
                            answerViewWinner,
                            scoreView,
                            winnerName,
                            imageWinnerPhoto
                        )

                        setWinnerInfo(
                            0,
                            frameProfileDraw,
                            answerViewWinnerDraw,
                            scoreViewDraw,
                            winnerNameDraw,
                            imageWinnerPhotoDraw
                        )

                        val zoomanim = AnimationUtils.loadAnimation(this, R.anim.zoom)
                        imageWinnerSign!!.visibility = ImageView.VISIBLE
                        imageWinnerSign!!.startAnimation(zoomanim)
                        imageLoserSign!!.visibility = ImageView.VISIBLE
                        imageLoserSign!!.startAnimation(zoomanim)

                    }
                    game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp0
                    ).answerScore == game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                        GameManager.opp1
                    ).answerScore
                    -> {
                        deuce = true
                        secondName = true
                        setWinnerInfo(
                            0,
                            frameProfile,
                            answerViewWinner,
                            scoreView,
                            winnerName,
                            imageWinnerPhoto
                        )
                        setWinnerInfo(
                            1,
                            frameProfileDraw,
                            answerViewWinnerDraw,
                            scoreViewDraw,
                            winnerNameDraw,
                            imageWinnerPhotoDraw
                        )
                        answerViewWinnerFrameDraw?.visibility = RelativeLayout.VISIBLE
                        scoreViewDraw?.visibility = RelativeLayout.VISIBLE
                        imageWinnerSign?.visibility = ImageView.INVISIBLE
                        imageLoserSign?.visibility = ImageView.INVISIBLE
                        imageShot?.visibility = ImageView.INVISIBLE
                        imageAndIcon?.visibility = TextView.VISIBLE
                        val zoomanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                        val interpolator = BounceInterpolator(0.2, 10.0)
                        zoomanim.interpolator = interpolator
                        imageAndIcon!!.startAnimation(zoomanim)
                    }
                }

            }
    }


    /**
     * Get Winner-infos by 'setWinner' and Display Winner-Informations (name, scorinf, photo)
     * **/
    private fun setWinnerInfo(
        winnerIndex: Int,
        frameView: View?,
        answerView: TextView?,
        scoreView: TextView?,
        nameView: TextView?,
        profileView: ImageView?
    ) {
        frameView?.visibility = View.VISIBLE

        answerView?.text =
            game.playrounds.getValue("round${game.activeRound}").opponents.getValue("opponent$winnerIndex").answer
        scoreView?.text =
            ("+" + game.playrounds.getValue("round${game.activeRound}").opponents.getValue("opponent$winnerIndex").answerScore.toString())

        dbUsers.document(
            game.playrounds.getValue("round${game.activeRound}").opponents.getValue(
                "opponent$winnerIndex"
            ).userID.toString()
        )
            .get()
            .addOnSuccessListener {
                val winner = it.toObject(UserQP::class.java)!!
                nameView?.text = winner.userName
                setProfilePicture(winner, profileView)
                val shakehanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in_and_shake)

                scoreView!!.startAnimation(shakehanim)
                if (game.partyMode){
                    val drnk = (0..game.drinks.size-1).random()
                    if (deuce == true){
                        if (secondName == true){
                            winnerNames += winner.userName + " & "
                            secondName = false
                        } else {
                            winnerNames += winner.userName + " "
                            drinkView?.text = (winnerNames + "have this challenge: \n" + game.drinks[drnk])
                        }
                    } else {
                        winnerNames = winner.userName + " "
                        drinkView?.text = (winnerNames + "has this challenge: \n" + game.drinks[drnk])
                    }
                }
            }
    }


    private fun setProfilePicture(player: UserQP, profileView: ImageView?) {

        val storageRef = FirebaseStorage.getInstance().reference

        if (player.photo == null) {
            player.photo = DBMethods.defaultGuestImg
        }

        val spaceRef = storageRef.child(player.photo!!)
        spaceRef.downloadUrl
            .addOnSuccessListener { uri ->
                if (profileView != null) {
                    Glide
                        .with(applicationContext)
                        .load(uri)
                        .into(profileView)
                }
            }.addOnFailureListener { Log.d("Test", " Failed!") }
    }


}