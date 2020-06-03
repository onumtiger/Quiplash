package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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


        val ref = FirebaseDatabase.getInstance().getReference().child("active_games")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    for (game in p0.children) {
                        val activeGame =  game.getValue(Game::class.java)
                        gameList.add(activeGame!!)
                    }
                    val adapter = GameListAdapter(applicationContext, R.layout.active_game_list_item, gameList)
                    activeGamesList.adapter = adapter
                }
            }
        })


    }
}
