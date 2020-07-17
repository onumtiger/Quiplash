package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.quiplash.BounceInterpolator
import com.example.quiplash.LandingActivity
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class GameLaunchingActivity : AppCompatActivity() {

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        setContentView(R.layout.activity_game_launching)

        //Set View-Elements
        val gamelaunchTitle = findViewById<TextView>(R.id.textViewTitleGL)
        val layoutGamelaunch = findViewById<ConstraintLayout>(R.id.layoutGameLaunch)

        //If it's not the first Round edit Text
        if (game.activeRound > 1) {
            gamelaunchTitle.text = getString(R.string.next_round_starts)
        }

        //Set Animation
        val splashanim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val interpolator = BounceInterpolator(0.5, 10.0)
        splashanim.interpolator = interpolator
        layoutGamelaunch.startAnimation(splashanim)

        //If Game has begun allocate Player If Game is already over send player back to Landing-View
        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                if (game.activeRound < game.playrounds.size) {
                    GameManager.playerAllocation(
                        this.applicationContext,
                        auth!!.currentUser?.uid.toString()
                    )
                } else {
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
                }

            }

    }

    /**
     * disable backButton on device
     */
    override fun onBackPressed() {
        println("do nothing")
    }
}