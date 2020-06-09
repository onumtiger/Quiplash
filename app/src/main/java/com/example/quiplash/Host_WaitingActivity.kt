package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.DBMethods.DBCalls.Companion.deleteGame
import com.example.quiplash.DBMethods.DBCalls.Companion.getCurrentGame
import com.example.quiplash.DBMethods.DBCalls.Companion.getUserWithID
import com.google.firebase.auth.FirebaseAuth
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class Host_WaitingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvite_Players = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val playersListView = findViewById<ListView>(R.id.players_list)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        auth = FirebaseAuth.getInstance()

        getUsersList(playersListView, game.gameID, btnStartGame, btnEndGame, btnLeaveGame)

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        btnStartGame.setOnClickListener {
            createAllRounds()
        }

        btnEndGame.setOnClickListener {
            deleteGame(game.gameID)
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        btnLeaveGame.setOnClickListener {
            // removeUserFromGame(gameID, auth.currentUser?.uid.toString())
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        refreshLayout.setOnRefreshListener {
            getUsersList(playersListView, game.gameID, btnStartGame, btnEndGame, btnLeaveGame)
            refreshLayout.isRefreshing = false
        }

        btnInvite_Players.setOnClickListener {
            val dialogFragment = Invite_Player()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("invite")
            if (prev != null) {
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

    fun getUsersList(
        playersListView: ListView,
        gameID: String,
        btnStartGame: Button,
        btnEndGame: Button,
        btnLeaveGame: Button
    ) {
        val playersNames = mutableListOf<String>()
        var userIDList = mutableListOf<String>()
        var currentGame: Game
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                currentGame = result
                setBtnVisibility(currentGame, btnStartGame, btnEndGame, btnLeaveGame)
                var playerNumber = currentGame.playerNumber
                var currentPlayerNumber = currentGame.users.size
                val players = currentGame.users
                userIDList = players.toMutableList()
                userIDList.forEach {
                    val callbackUser = object : Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
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
        getCurrentGame(callback, gameID)
    }


    fun setBtnVisibility(
        game: Game,
        btnStartGame: Button,
        btnEndGame: Button,
        btnLeaveGame: Button
    ) {
        if (game.users.contains(auth.currentUser?.uid.toString())) {
            btnStartGame.visibility = View.VISIBLE
            btnEndGame.visibility = View.VISIBLE
            btnLeaveGame.visibility = View.INVISIBLE
        } else {
            btnStartGame.visibility = View.INVISIBLE
            btnEndGame.visibility = View.INVISIBLE
            btnLeaveGame.visibility = View.VISIBLE
        }
    }

    /**
     * All Rounds will be created here before game actually starts.
     * At this point users-count is definitely sure and known.
     * By the knowledge of participants-count and rounds, every round can be created.
     * In a game round, each player competes once against each player in a duel to answer a question.
     * In addition, the rounds created are multiplied by the required number of rounds.
     * For example:
     * Players = 3
     * Rounds = 3
     * [Round1.1: competeters = user1 & user2, voters = user3;
     * Round1.2: competeters = user2 & user3; voters = user1;
     * Round1.3: competeters = user1 & user3; voters = user2]
     * --> This is one Round, so that it stays fair ;)
     * --> total rounds = 9
     * **/
    fun createAllRounds() {
        var allRoundCount = 1
        var jump = 1
        var roundCount = 0
        val oneRound: MutableList<Round> = mutableListOf()
        val allRounds: MutableList<Round> = mutableListOf()

        while (jump < game.users.size) {

            while (roundCount < game.users.size - jump) {

                val voters = mutableListOf<String>()
                var votersarr = arrayOf<String>()
                for (user in game.users) {

                    if (game.users.indexOf(user) != roundCount && game.users.indexOf(user) != (roundCount + jump)) {
                        voters += user
                        votersarr += user
                    }
                }
                oneRound += (Round(
                    listOf(game.users[roundCount], game.users[(roundCount + jump)]),
                    voters
                ))

                roundCount += 1

            }
            roundCount = 0
            jump += 1
        }


        while (allRoundCount <= game.rounds) {
            allRounds += oneRound
            allRoundCount += 1
        }

        game.playrounds = allRounds
        db.document(game.gameID)
            .set(game)
            .addOnSuccessListener {
                Log.d("Success", "DocumentSnapshot successfully written!")

                //myWebSocketClient.send(game.gameID)

                val intent = Intent(this, Game_LaunchingActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }

    }
}