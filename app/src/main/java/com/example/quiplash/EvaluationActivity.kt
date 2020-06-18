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
    private var answerViewWinnerDraw: TextView? = null
    private var winnerNameDraw: TextView? = null
    private var imageWinnerPhotoDraw: ImageView? = null
    private var scoreViewDraw: TextView? = null
    private var answerViewWinnerFrameDraw: View? = null
    private var answerViewWinnerFrame: View? = null
    private var winnerFrame: RelativeLayout? = null

    private var playerPhoto = ""
    lateinit var awaitNextRound: ListenerRegistration


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        dbUsers = FirebaseFirestore.getInstance().collection(dbUsersPath)
        auth = FirebaseAuth.getInstance()

        setPoints()
        setContentView(R.layout.activity_evaluation)


        val questionEval = findViewById<TextView>(R.id.questionEval)
        answerViewWinner = findViewById(R.id.answerRoundWinner)
        winnerName = findViewById(R.id.textRoundWinnerName)
        imageWinnerPhoto = findViewById(R.id.imageRoundWinnerPhoto)
        winnerNameDraw = findViewById(R.id.textRoundWinnerNameDraw)
        answerViewWinnerFrameDraw = findViewById(R.id.viewDraw)
        imageWinnerPhotoDraw = findViewById(R.id.imageRoundWinnerPhotoDraw)
        val roundViewEval = findViewById<TextView>(R.id.roundsEval)
        scoreView = findViewById(R.id.textViewScore)
        scoreViewDraw = findViewById(R.id.textViewScoreDraw)
        winnerFrame = findViewById(R.id.winnerDraw)
        val nextBtn = findViewById<TextView>(R.id.buttonNext)
        val oldRound = game.activeRound


        questionEval.text = game.playrounds[game.activeRound - 1].question
        roundViewEval.text = "${ceil(game.activeRound.toDouble()/3).toInt()} / ${game.rounds}"


        if (game.hostID == auth!!.currentUser?.uid) {
            nextBtn.visibility = View.VISIBLE
        }

        if(game.activeRound == game.playrounds.size){
            nextBtn.text = "Show Scoreboard"
        }

        nextBtn.setOnClickListener {
            game.activeRound = game.activeRound +1
            db.document(game.gameID)
                .set(game)
                .addOnSuccessListener {
                    gotoNextRound()
                }
                .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
        }




        awaitNextRound = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("SUCCESS", "Current data: ${snapshot.data}")
                Log.d("SUCCESS", "Current data: $oldRound")

                game = snapshot.toObject(Game::class.java)!!
                if (game.activeRound > oldRound) { //is activeRound new?
                    gotoNextRound()
                }

            }
        }


    }



    private fun gotoNextRound(){
        awaitNextRound.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        if(game.activeRound <= game.playrounds.size){
            val intent = Intent(this, GameLaunchingActivity::class.java)
            startActivity(intent)
        } else{
            val intent = Intent(this, End_Of_GameActivity::class.java)
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
                    setWinnerInfo(0, answerViewWinnerFrame, answerViewWinner, scoreView, winnerName, imageWinnerPhoto)
                } else if (game.playrounds[game.activeRound - 1].opponents[0].answerScore < game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    setWinnerInfo(1, answerViewWinnerFrame, answerViewWinner, scoreView, winnerName, imageWinnerPhoto)
                } else if (game.playrounds[game.activeRound - 1].opponents[0].answerScore == game.playrounds[game.activeRound - 1].opponents[1].answerScore) {
                    setWinnerInfo(0, answerViewWinnerFrame, answerViewWinner, scoreView, winnerName, imageWinnerPhoto)
                    setWinnerInfo(1, answerViewWinnerFrameDraw, answerViewWinnerDraw, scoreViewDraw, winnerNameDraw, imageWinnerPhotoDraw)
                    winnerFrame?.visibility = RelativeLayout.VISIBLE
                }

            }
    }


    @SuppressLint("SetTextI18n")
    private fun setWinnerInfo(winnerIndex: Int, frameView: View?, answerView: TextView?, scoreView: TextView?, nameView: TextView?, profileView: ImageView?) {
        frameView?.visibility = View.VISIBLE
        answerView?.text =game.playrounds[game.activeRound - 1].opponents[winnerIndex].answer
        scoreView?.text = "+" + game.playrounds[game.activeRound - 1].opponents[winnerIndex].answerScore.toString()
        dbUsers.document(game.playrounds[game.activeRound - 1].opponents[winnerIndex].userID.toString())
            .get()
            .addOnSuccessListener {
                val winner = it.toObject(UserQP::class.java)
                nameView?.text = winner!!.userName
                setProfilePicture(winner, profileView)

            }
    }


    private fun setProfilePicture(player: UserQP, profileView: ImageView?){

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