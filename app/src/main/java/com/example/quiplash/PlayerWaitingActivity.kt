package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quiplash.GameManager.Companion.game

class PlayerWaitingActivity  : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamessPath = "games"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_waiting)
        db = FirebaseFirestore.getInstance().collection(dbGamessPath)

        val docRef = db.document(game.gameID)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("SUCCESS", "Current data: ${snapshot.data}")
                game = snapshot.toObject(Game::class.java)!!
                if(game.playrounds.size <0){
                    val intent = Intent(this, Game_LaunchingActivity::class.java)
                    startActivity(intent)
                }

            } else {
                Log.d("ERROR", "Current data: null")
                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
            }
        }
    }




}