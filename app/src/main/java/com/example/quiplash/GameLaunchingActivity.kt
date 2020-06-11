package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.getQuestions
import com.example.quiplash.DBMethods.DBCalls.Companion.getUsers
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.quiplash.GameManager.Companion.game


class GameLaunchingActivity : AppCompatActivity() {

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    var all_questions: ArrayList<Question>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_game_launching)



        val callback = object: Callback<ArrayList<Question>> {
            override fun onTaskComplete(result: ArrayList<Question>) {
                all_questions = result
            }
        }
        getQuestions(callback)




        if (game.playrounds[game.activeRound-1].voters.contains(auth!!.currentUser?.uid )){
            // val intent = Intent(this, Choose_AnswerActivity::class.java)
            // startActivity(intent)
            val intent = Intent(this, PrepareAnswerActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, PrepareAnswerActivity::class.java)
            startActivity(intent)
        }

    }
}