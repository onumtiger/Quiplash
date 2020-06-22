package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.ceil
import com.example.quiplash.GameMethods.Companion.startTimer

class EvaluationActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    lateinit var dbUsers: CollectionReference
    private val dbGamesPath = "games"
    private val dbUsersPath = "users"
    private var auth: FirebaseAuth? = null

    //Views
    private var answerViewWinner: TextView? = null
    private var winnerName: TextView? = null
    private var imageWinnerPhoto: ImageView? = null
    private var scoreView: TextView? = null
    private var frameProfile: RelativeLayout? = null

    private var answerViewWinnerDraw: TextView? = null
    private var winnerNameDraw: TextView? = null
    private var imageWinnerPhotoDraw: ImageView? = null
    private var scoreViewDraw: TextView? = null
    private var answerViewWinnerFrameDraw: View? = null
    private var frameProfileDraw: RelativeLayout? = null

    private var playerPhoto = ""
    lateinit var awaitNextRound: ListenerRegistration
    private var nextroundFlag = false
    var oldRound = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        dbUsers = FirebaseFirestore.getInstance().collection(dbUsersPath)
        auth = FirebaseAuth.getInstance()

        setPoints()
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_evaluation)


        val questionEval = findViewById<TextView>(R.id.questionEval)
        val textViewTimer = findViewById<TextView>(R.id.timerViewEval)
        answerViewWinner = findViewById(R.id.answerRoundWinner)
        winnerName = findViewById(R.id.textRoundWinnerName)
        imageWinnerPhoto = findViewById(R.id.imageRoundWinnerPhoto)
        scoreView = findViewById(R.id.textViewScore)
        frameProfile = findViewById(R.id.winner)

        answerViewWinnerDraw = findViewById(R.id.answerRoundWinnerDraw)
        winnerNameDraw = findViewById(R.id.textRoundWinnerNameDraw)
        answerViewWinnerFrameDraw = findViewById(R.id.viewDraw)
        imageWinnerPhotoDraw = findViewById(R.id.imageRoundWinnerPhotoDraw)
        val roundViewEval = findViewById<TextView>(R.id.roundsEval)
        scoreViewDraw = findViewById(R.id.textViewScoreDraw)
        frameProfileDraw = findViewById(R.id.winnerDraw)

        val nextBtn = findViewById<TextView>(R.id.buttonNext)

        oldRound = game.activeRound


        questionEval.text = game.playrounds[game.activeRound - 1].question
        roundViewEval.text = "${ceil(game.activeRound.toDouble() / 3).toInt()} / ${game.rounds}"

        val callbackTimer = object : Callback<Boolean> {
            override fun onTaskComplete(result: Boolean) {
                Log.d("TIMER", "finished? = $result")
                setNextRound()
            }
        }
        startTimer(textViewTimer, GameManager.startSeconds, callbackTimer)


        if (game.activeRound == game.playrounds.size) {
            nextBtn.text = getString(R.string.show_scoreboard)
        }

        nextBtn.setOnClickListener {
            Sounds.playClickSound(this)

            setNextRound()
        }




        awaitNextRound = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.activeRound > oldRound) { //is activeRound new?
                    if(!nextroundFlag){
                        gotoNextRound()
                    }

                }

            }
        }


    }


    private fun setNextRound() {
        game.activeRound = game.activeRound + 1

        db.document(game.gameID)
            .update("activeRound", oldRound+1)
            .addOnSuccessListener { Log.d("SUCCESS", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }


        /*db.document(game.gameID)
            .set(game)
            .addOnSuccessListener {
                gotoNextRound()
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }*/
    }

    private fun gotoNextRound() {
        awaitNextRound.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        nextroundFlag = true
        Log.d("EVALUTATION", " oldRound = $oldRound")
        Log.d("EVALUTATION", " rundengröße = ${game.playrounds.size}")
        Log.d("EVALUTATION", " result = ${oldRound >= game.playrounds.size}")

        if ( oldRound < game.playrounds.size) {
            GameMethods.playerAllocation(this.applicationContext, auth!!.currentUser?.uid.toString())
        } else {
            val intent = Intent(this, EndOfGameActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        println("do nothing")
    }

    private fun setPoints() {
        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!

                game.playrounds[game.activeRound - 1].voters.forEach {
                    if (it.voteUserID == game.playrounds[game.activeRound - 1].opponents[0].userID) {
                        game.playrounds[game.activeRound - 1].opponents[0].answerScore += 10
                    } else if (it.voteUserID == game.playrounds[game.activeRound - 1].opponents[1].userID) {
                        game.playrounds[game.activeRound - 1].opponents[1].answerScore += 10
                    }
                }
                if (game.playrounds[game.activeRound - 1].opponents[0].answerScore > game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    game.playrounds[game.activeRound - 1].opponents[0].answerScore += 50
                } else if (game.playrounds[game.activeRound - 1].opponents[0].answerScore < game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    game.playrounds[game.activeRound - 1].opponents[1].answerScore += 50
                }

                db.document(game.gameID)
                    .set(game)
                    .addOnSuccessListener {
                        getWinner()
                    }
                    .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
                return@addOnSuccessListener

            }


    }


    private fun getWinner() {
        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                if (game.playrounds[game.activeRound - 1].opponents[0].answerScore > game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    setWinnerInfo(
                        0,
                        frameProfile,
                        answerViewWinner,
                        scoreView,
                        winnerName,
                        imageWinnerPhoto
                    )
                } else if (game.playrounds[game.activeRound - 1].opponents[0].answerScore < game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    setWinnerInfo(
                        1,
                        frameProfileDraw,
                        answerViewWinner,
                        scoreView,
                        winnerName,
                        imageWinnerPhoto
                    )
                } else if (game.playrounds[game.activeRound - 1].opponents[0].answerScore == game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
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
                }

            }
    }


    @SuppressLint("SetTextI18n")
    private fun setWinnerInfo(
        winnerIndex: Int,
        frameView: View?,
        answerView: TextView?,
        scoreView: TextView?,
        nameView: TextView?,
        profileView: ImageView?
    ) {
        frameView?.visibility = View.VISIBLE
        answerView?.text = game.playrounds[game.activeRound - 1].opponents[winnerIndex].answer
        scoreView?.text =
            "+" + game.playrounds[game.activeRound - 1].opponents[winnerIndex].answerScore.toString()
        dbUsers.document(game.playrounds[game.activeRound - 1].opponents[winnerIndex].userID.toString())
            .get()
            .addOnSuccessListener {
                val winner = it.toObject(UserQP::class.java)
                nameView?.text = winner!!.userName
                setProfilePicture(winner, profileView)

            }
    }


    private fun setProfilePicture(player: UserQP, profileView: ImageView?) {

        val storageRef = FirebaseStorage.getInstance().reference

        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        val spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener { uri ->
                if (profileView != null) {
                    Glide
                        .with(this)
                        .load(uri)
                        .into(profileView)
                }
            }.addOnFailureListener { Log.d("Test", " Failed!") }
    }


}