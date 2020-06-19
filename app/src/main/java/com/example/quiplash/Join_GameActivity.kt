package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.DBMethods.DBCalls.Companion.getActiveGames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quiplash.GameManager.Companion.game


class Join_GameActivity : AppCompatActivity() {
    lateinit var gameList: MutableList<Game>
    private lateinit var auth: FirebaseAuth

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_join_game)


        val btnNewGameActivity = findViewById<AppCompatImageButton>(R.id.join_new_game_btn)
        val btnBack = findViewById<AppCompatImageButton>(R.id.join_game_go_back_arrow)
        val activeGamesList = findViewById<ListView>(R.id.active_games_list)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)

        btnNewGameActivity.setOnClickListener {
            val intent = Intent(this, New_GameActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        refreshLayout.setOnRefreshListener {
            getGamesList(activeGamesList)
            refreshLayout.isRefreshing = false
        }

        getGamesList(activeGamesList)
        activeGamesList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            // Get the selected item text from ListView
            val selectedItem = parent.getItemAtPosition(position) as Game

            db.document(selectedItem.gameID).get()
                .addOnSuccessListener { documentSnapshot ->
                    try {
                        game = documentSnapshot.toObject(Game::class.java)!!
                    } finally {
                        val intent = Intent(this, HostWaitingActivity::class.java)
                        intent.putExtra("gameID", selectedItem.gameID)
                        startActivity(intent)
                    }

                }
        }
    }

    fun getGamesList(activeGamesList: ListView) {
        gameList = mutableListOf()
        val callback = object: Callback<MutableList<Game>> {
            override fun onTaskComplete(result: MutableList<Game>) {
                gameList = result
                val adapter = GameListAdapter(applicationContext, R.layout.active_game_list_item, gameList)
                activeGamesList.adapter = adapter
            }
        }
        getActiveGames(callback, gameList)
    }
}


