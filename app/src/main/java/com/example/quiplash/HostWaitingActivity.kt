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


class HostWaitingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvitePlayers = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val btnJoinGame = findViewById<Button>(R.id.join_game_btn)
        val playersListView = findViewById<ListView>(R.id.players_list)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        auth = FirebaseAuth.getInstance()

        getUsersList(playersListView, game.gameID)

        btnBack.setOnClickListener {
            Sounds.playClickSound(this)

            super.onBackPressed()
        }

        btnStartGame.setOnClickListener {
            Sounds.playClickSound(this)

            createAllRounds()
        }

        btnEndGame.setOnClickListener {
            Sounds.playClickSound(this)

            val callbackSuccess = object : Callback<Boolean> {
                override fun onTaskComplete(result: Boolean) {
                    Log.d("GAMEDELETE", "deleted? = $result")
                    val intent = Intent(this@HostWaitingActivity, LandingActivity::class.java)
                    startActivity(intent)
                }
            }
            deleteGame(game.gameID,callbackSuccess)
        }

        btnLeaveGame.setOnClickListener {
            removeUserFromGame()
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        btnJoinGame.setOnClickListener {
            addUserToGame()
            btnLeaveGame.visibility = View.VISIBLE
            btnJoinGame.visibility = View.INVISIBLE
            getUsersList(playersListView, game.gameID)
            refreshLayout.isRefreshing = false
        }

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            getUsersList(playersListView, game.gameID)
            refreshLayout.isRefreshing = false
        }

        btnInvitePlayers.setOnClickListener {
            val callbackUser = object : Callback<UserQP> {
                override fun onTaskComplete(result: UserQP) {
                    val user = result
                    if (user.guest!!) {
                        val dialogFragment = Invite_Player()
                        val ft = supportFragmentManager.beginTransaction()
                        val prev = supportFragmentManager.findFragmentByTag("invite")
                        if (prev != null) {
                            ft.remove(prev)
                        }
                        ft.addToBackStack(null)
                        dialogFragment.show(ft, "invite")
                    } else {
                        seeFriendsList()
                    }
                }
            }
            getUserWithID(callbackUser, auth.currentUser?.uid.toString())

        }
    }
    fun seeFriendsList() {
        val intent = Intent(this, InviteFriendsToGameActivity::class.java)
        intent.putExtra("gameID", game.gameID)
        startActivity(intent)
    }

    fun addUserToGame() {
        val selectedItem = game
        selectedItem.users = selectedItem.users + auth.currentUser?.uid.toString()
        DBMethods.DBCalls.updateGameUsers(selectedItem)
    }

    fun removeUserFromGame() {
        val selectedItem = game
        val filteredUsers = selectedItem.users.filterIndexed { _, s -> (s != auth.currentUser?.uid.toString())  }
        selectedItem.users = filteredUsers
        DBMethods.DBCalls.updateGameUsers(selectedItem)
    }

    fun setBtnVisibility(currentGame: Game, currentPlayerNumber: Int, playerNumber: Int) {
        val btnInvitePlayers = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val btnJoinGame = findViewById<Button>(R.id.join_game_btn)

        if (currentGame.users[0] == auth.currentUser?.uid.toString()) {
            btnStartGame.visibility = View.VISIBLE
            btnEndGame.visibility = View.VISIBLE
            btnLeaveGame.visibility = View.INVISIBLE
            btnJoinGame.visibility = View.INVISIBLE
            btnInvitePlayers.visibility = View.VISIBLE
        } else if (currentGame.users.contains(auth.currentUser?.uid.toString())) {
            btnStartGame.visibility = View.INVISIBLE
            btnEndGame.visibility = View.INVISIBLE
            btnLeaveGame.visibility = View.VISIBLE
            btnJoinGame.visibility = View.INVISIBLE
            btnInvitePlayers.visibility = View.VISIBLE
        } else {
            btnStartGame.visibility = View.INVISIBLE
            btnEndGame.visibility = View.INVISIBLE
            btnLeaveGame.visibility = View.INVISIBLE
            btnJoinGame.visibility = View.VISIBLE
            btnInvitePlayers.visibility = View.INVISIBLE
        }

        if (currentPlayerNumber == playerNumber) {
            btnStartGame.isClickable = true
            btnStartGame.setBackgroundResource(R.color.colorButtonGreen)
            btnInvitePlayers.visibility = View.INVISIBLE
        } else {
            btnStartGame.isClickable = false
            btnStartGame.setBackgroundResource(R.color.colorGray)
        }
    }

    fun getUsersList(
        playersListView: ListView,
        gameID: String
    ) {
        val playersNames = mutableListOf<UserQP>()
        var userIDList: MutableList<String>
        var currentGame: Game
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                currentGame = result
                val playerNumber = currentGame.playerNumber
                val currentPlayerNumber = currentGame.users.size
                val players = currentGame.users
                userIDList = players.toMutableList()
                userIDList.forEach {
                    val callbackUser = object : Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            val user = result
                            playersNames.add(user)
                            val adapter = PlayersListAdapter(
                                applicationContext,
                                R.layout.host_waiting_list_item,
                                playersNames
                            )
                            playersListView.adapter = adapter
                            setBtnVisibility(currentGame, currentPlayerNumber, playerNumber)
                        }
                    }
                    getUserWithID(callbackUser, it)
                }
            }
        }
        getCurrentGame(callback, gameID)
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

                val voters = mutableListOf<Voter>()
                for (user in game.users) {

                    if (game.users.indexOf(user) != roundCount && game.users.indexOf(user) != (roundCount + jump)) {
                        voters += Voter(user)
                    }
                }
                oneRound += (Round(voters,
                    listOf(Opponent( game.users[roundCount]), Opponent( game.users[(roundCount + jump)]))
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
            .update("playrounds", allRounds)
            .addOnSuccessListener {
                //myWebSocketClient.send(game.gameID)

                val intent = Intent(this, GameLaunchingActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }

    }
}