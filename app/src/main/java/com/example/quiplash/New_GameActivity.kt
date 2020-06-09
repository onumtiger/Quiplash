package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.setGame
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class New_GameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<AppCompatImageButton>(R.id.new_game_go_back_arrow)
        val btnStart = findViewById<Button>(R.id.start_game)


        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnStart.setOnClickListener() {
            createNewGame()
            val intent = Intent(this, Host_WaitingActivity::class.java);
            intent.putExtra("gameID", createNewGame())
            startActivity(intent);
        }
    }

    fun createNewGame(): String {
        val categorySpinner: Spinner = findViewById<Spinner>(R.id.category_dropdown)
        val playerSpinner: Spinner = findViewById<Spinner>(R.id.player_dropdown)
        val roundsSpinner: Spinner = findViewById<Spinner>(R.id.rounds_dropdown)

        val category = categorySpinner.selectedItem.toString()
        val playerNumbersSpinner = playerSpinner.selectedItem.toString()
        val playerNumbers = playerNumbersSpinner.substringBefore(' ').toInt()
        val roundSpinner = roundsSpinner.selectedItem.toString()
        val rounds = roundSpinner.substringBefore(' ').toInt()
        val activeRound = 1
        val users: ArrayList<String> = arrayListOf(auth.currentUser?.uid.toString())
        val gameID = ""

        val newGame = Game(activeRound, category, playerNumbers, rounds, users, gameID)
        return setGame(newGame)
    }
}
