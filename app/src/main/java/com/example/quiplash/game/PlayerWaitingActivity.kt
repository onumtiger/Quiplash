package com.example.quiplash.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.database.DBMethods
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.LandingActivity
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.firestore.ListenerRegistration

class PlayerWaitingActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamessPath = DBMethods.gamesPath

    lateinit var awaitGamestart: ListenerRegistration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_player_waiting)
        db = FirebaseFirestore.getInstance().collection(dbGamessPath)

        awaitGamestart = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.size < 0) {
                    gotoGameLaunch()
                }

            } else {
                gotoGameLanding()
            }
        }

    }


    private fun gotoGameLaunch() {
        awaitGamestart.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        Sounds.playStartSound(this)
        val intent = Intent(this, GameLaunchingActivity::class.java)
        startActivity(intent)
    }

    private fun gotoGameLanding() {
        awaitGamestart.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
    }
}