package com.example.quiplash.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.database.DBMethods.Companion.getActiveGames
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP


class JoinGameActivity : AppCompatActivity() {
    lateinit var gameList: MutableList<Game>
    private lateinit var auth: FirebaseAuth

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath

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
        val noActiveGameInfo = findViewById<TextView>(R.id.no_active_game)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)

        noActiveGameInfo.visibility = View.INVISIBLE

        // set clickListeners for all buttons & refreshListener for view
        btnNewGameActivity.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, NewGameActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            Sounds.playClickSound(this)
            super.onBackPressed()
        }

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            getGamesList(activeGamesList)
            refreshLayout.isRefreshing = false
        }

        activeGamesList.viewTreeObserver.addOnScrollChangedListener {
            if (!activeGamesList.canScrollVertically(1)) {
                // Bottom of scroll view, disable refreshLayout
                refreshLayout.isEnabled = false
            }
            if (!activeGamesList.canScrollVertically(-1)) {
                // Top of scroll view, enable refreshLayout
                refreshLayout.isEnabled = true
            }
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
                        val intent = Intent(this, WaitingActivity::class.java)
                        intent.putExtra("gameID", selectedItem.gameID)
                        startActivity(intent)
                    }

                }
        }
    }


    /**
     * Show all active and public games
     */
    fun getGamesList(activeGamesList: ListView) {
        val noActiveGameInfo = findViewById<TextView>(R.id.no_active_game)
        var resultGames = mutableListOf<Game>()
        gameList = mutableListOf<Game>()
        val callback = object:
            Callback<MutableList<Game>> {
            override fun onTaskComplete(result: MutableList<Game>) {
                gameList = result
                if (gameList.isNullOrEmpty()) {
                    noActiveGameInfo.visibility = View.VISIBLE
                } else {
                    gameList.forEach {
                        if (it.isPublic || (it.users.contains(auth.currentUser?.uid.toString())) || it.invitations.contains(auth.currentUser?.uid.toString())) {
                            resultGames.add(it)
                        }
                    }
                }
                val adapter = GameListAdapter(
                    applicationContext,
                    R.layout.active_game_list_item,
                    resultGames
                )
                activeGamesList.adapter = adapter
            }
        }
        getActiveGames(callback, gameList)
    }
}