package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class Join_GameActivity : AppCompatActivity() {
    lateinit var gameList: MutableList<Game>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val btnNewGameActivity = findViewById<AppCompatImageButton>(R.id.join_new_game_btn)
        val btnBack = findViewById<AppCompatImageButton>(R.id.join_game_go_back_arrow)
        val activeGamesList = findViewById<ListView>(R.id.active_games_list)
        gameList = mutableListOf()

        btnNewGameActivity.setOnClickListener() {
            val intent = Intent(this, New_GameActivity::class.java);
            startActivity(intent);
        }

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

       /* val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("games").document("MvFTny0fZz3dFtiiqYtr")
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    Log.d("exists", "DocumentSnapshot data ${documentSnapshot.data}")
                    val activeGame = documentSnapshot.toObject(Game::class.java)
                    gameList.add(activeGame!!)
                } else {
                    Log.d("does not exist", "no such data")
                }

                val adapter = GameListAdapter(applicationContext, R.layout.active_game_list_item, gameList)
                activeGamesList.adapter = adapter
            }*/

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("games")
        docRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val activeGame = document.toObject(Game::class.java)
                    gameList.add(activeGame!!)
                    val adapter = GameListAdapter(applicationContext, R.layout.active_game_list_item, gameList)
                    activeGamesList.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)

            }

    }
}
