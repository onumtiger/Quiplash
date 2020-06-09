package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.DBMethods.DBCalls.Companion.deleteGame
import com.example.quiplash.DBMethods.DBCalls.Companion.getCurrentGame
import com.example.quiplash.DBMethods.DBCalls.Companion.getUserWithID
import com.example.quiplash.DBMethods.DBCalls.Companion.removeUserFromGame
import com.google.firebase.auth.FirebaseAuth

class Host_WaitingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvite_Players = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val playersListView = findViewById<ListView>(R.id.players_list)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        auth = FirebaseAuth.getInstance()

        val gameID: String? = intent.getStringExtra("gameID")
        getUsersList(playersListView, gameID!!, btnStartGame, btnEndGame, btnLeaveGame)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnStartGame.setOnClickListener() {
            startGame()
        }

        btnEndGame.setOnClickListener() {
            deleteGame(gameID)
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent)
        }

        btnLeaveGame.setOnClickListener() {
           // removeUserFromGame(gameID, auth.currentUser?.uid.toString())
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent)
        }

        refreshLayout.setOnRefreshListener() {
            getUsersList(playersListView, gameID!!, btnStartGame, btnEndGame, btnLeaveGame)
            refreshLayout.isRefreshing = false
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

    fun setStartBtn(currentPlayerNumber: Int, playerNumber: Int, btnStartGame: Button) {
        if (currentPlayerNumber == playerNumber) {
            btnStartGame.isClickable = true
            btnStartGame.setBackgroundResource(R.color.colorButtonGreen)
        } else {
            btnStartGame.isClickable = false
            btnStartGame.setBackgroundResource(R.color.colorGray)
        }
    }

    fun getUsersList(playersListView: ListView, gameID: String, btnStartGame: Button, btnEndGame: Button, btnLeaveGame: Button) {
        var playersNames = mutableListOf<String>()
        var userIDList = mutableListOf<String>()
        var currentGame: Game
        val callback = object: Callback<Game> {
            override fun onTaskComplete(result: Game) {
                currentGame = result
                setBtnVisibility(currentGame, btnStartGame, btnEndGame, btnLeaveGame)
                var playerNumber = currentGame.playerNumber
                var currentPlayerNumber = currentGame.users.size
                val players = currentGame?.users?.values
                if (players != null) {
                    userIDList = players.toMutableList()
                    userIDList.forEach{
                       val callbackUser = object : Callback<User> {
                           override fun onTaskComplete(result: User) {
                               var user = result
                               playersNames.add(user.userName!!)
                               val adapter = PlayersListAdapter(
                                   applicationContext,
                                   R.layout.host_waiting_list_item,
                                   playersNames
                               )
                               playersListView.adapter = adapter
                               setStartBtn(currentPlayerNumber, playerNumber, btnStartGame)
                           }
                       }
                        getUserWithID(callbackUser, it)
                    }
                }
            }
        }
        getCurrentGame(callback, gameID)
    }

    fun startGame() {
        val intent = Intent(this, Game_LaunchingActivity::class.java);
        startActivity(intent)
    }

    fun setBtnVisibility(game: Game, btnStartGame: Button, btnEndGame: Button, btnLeaveGame: Button) {
        if (game.users.getValue("userID1") ==  auth.currentUser?.uid.toString()) {
            btnStartGame.visibility = View.VISIBLE
            btnEndGame.visibility = View.VISIBLE
            btnLeaveGame.visibility = View.INVISIBLE
        } else {
            btnStartGame.visibility = View.INVISIBLE
            btnEndGame.visibility = View.INVISIBLE
            btnLeaveGame.visibility = View.VISIBLE
        }
    }
}