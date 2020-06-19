package com.example.quiplash

import android.content.Intent
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
                if(game.playrounds.size>= game.activeRound) {

                    if (game.playrounds[game.activeRound - 1].opponents[0].userID.equals(auth!!.currentUser?.uid.toString()) || game.playrounds[game.activeRound - 1].opponents[1].userID.equals(
                            auth!!.currentUser?.uid.toString()
                        )
                    ) {
                        val intent = Intent(this, PrepareAnswerActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, ChooseAnswerActivity::class.java)
                        startActivity(intent)
                    }
                } else{
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
                }
            }

    }



    override fun onBackPressed() {
        println("do nothing")
    }
}