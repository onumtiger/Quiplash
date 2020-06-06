package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.firestore.FirebaseFirestore

class Host_WaitingActivity : AppCompatActivity() {
    lateinit var playersList: MutableList<String>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvite_Players = findViewById<Button>(R.id.invite_players_btn)
        val playersListView = findViewById<ListView>(R.id.players_list)
        playersList = mutableListOf()
        var playerNumber = 0
        var currentPlayerNumber = 0

        val gameID= intent.getStringExtra("gameID")

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("games").document(gameID)
        println("docRef")
        println(docRef)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val game = document.toObject (Game::class.java)
                    if (game != null) {
                        playerNumber = game.playerNumber
                        currentPlayerNumber = game.users.size
                        val players = game?.users?.values
                        if (players != null) {
                            playersList = players.toMutableList()
                            Log.d("playersListSize", "${playersList.size}")
                        }
                        startGame(playerNumber, currentPlayerNumber)
                    }

                    val adapter = PlayersListAdapter(
                        applicationContext,
                        R.layout.host_waiting_list_item,
                        playersList
                    )
                    playersListView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)

            }

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnInvite_Players.setOnClickListener(){
            val dialogFragment = Invite_Player()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("invite")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "invite")
        }

    }

    fun startGame(playerNumber: Int, currentPlayerNumber: Int) {
        if (currentPlayerNumber == playerNumber) {
            Handler().postDelayed({
                val intent = Intent(this, Game_LaunchingActivity::class.java);
                startActivity(intent)
            }, 3000)
        }
    }
}