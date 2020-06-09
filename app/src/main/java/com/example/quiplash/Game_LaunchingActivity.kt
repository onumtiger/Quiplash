package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.quiplash.GameManager.Companion.game


class Game_LaunchingActivity : AppCompatActivity() {

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_game_launching)

        if (game.playrounds[game.activeRound-1].opponents.contains(
                auth!!.currentUser?.uid
            )
        ){
            val intent = Intent(this, QuestionActivity::class.java);
            startActivity(intent)
        }else{
            val intent = Intent(this, Choose_AnswerActivity::class.java);
            startActivity(intent)
        }

    }
}