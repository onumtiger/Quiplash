package com.example.quiplash

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.getQuestions
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class GameLaunchingActivity : AppCompatActivity() {

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var allQuestions: ArrayList<Question>? = null

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_game_launching)

        val gamelaunchTitle = findViewById<TextView>(R.id.textViewTitleGL)

        if(game.activeRound >1){
            gamelaunchTitle.text = getString(R.string.next_round_starts)
        }

        val callback = object: Callback<ArrayList<Question>> {
            override fun onTaskComplete(result: ArrayList<Question>) {
                allQuestions = result
            }
        }
        getQuestions(callback)

        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!
                GameMethods.playerAllocation(this.applicationContext, auth!!.currentUser?.uid.toString())
            }

    }



    override fun onBackPressed() {
        println("do nothing")
    }
}