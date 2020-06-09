package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class Host_WaitingActivity : AppCompatActivity() {
    lateinit var playersList: MutableList<String>
    //lateinit var myWebSocketClient: MyWebSocketClient

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "testGames"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)

        //myWebSocketClient = MyWebSocketClient(URI("ws://localhost:8080"), Draft_17())
        //myWebSocketClient.connect()

        setContentView(R.layout.activity_host_waiting)

        val btnBack = findViewById<AppCompatImageButton>(R.id.host_waiting_go_back_arrow)
        val btnInvite_Players = findViewById<Button>(R.id.invite_players_btn)
        val playersListView = findViewById<ListView>(R.id.players_list)
        playersList = mutableListOf()


        val playerNumber: Int = game.playerNumber
        val currentPlayerNumber = game.users.size
        val players = game.users
        playersList = players.toMutableList()
        Log.d("playersListSize", "${playersList.size}")
        Log.d("playersListSize-ID", game.gameID)

        startGame(playerNumber, currentPlayerNumber)


        val adapter = PlayersListAdapter(
            applicationContext,
            R.layout.host_waiting_list_item,
            playersList
        )
        playersListView.adapter = adapter




        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnInvite_Players.setOnClickListener() {
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

    fun startGame(playerNumber: Int, currentPlayerNumber: Int) {
        if (currentPlayerNumber == playerNumber) {

            createAllRounds()
            /*Handler().postDelayed({
                val intent = Intent(this, Game_LaunchingActivity::class.java);
                startActivity(intent)
            }, 3000)*/
        }
    }


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

                val intent = Intent(this, Game_LaunchingActivity::class.java);
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }

    }


    inner class MyWebSocketClient(serverUri: URI?, draft: Draft?) :
        WebSocketClient(serverUri, draft) {

        override fun onError(ex: Exception?) {
            this@Host_WaitingActivity.runOnUiThread {
                Toast.makeText(
                    this@Host_WaitingActivity,
                    ex.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            this@Host_WaitingActivity.runOnUiThread {
                Toast.makeText(
                    this@Host_WaitingActivity,
                    "Opened",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            this@Host_WaitingActivity.runOnUiThread {
                Toast.makeText(
                    this@Host_WaitingActivity,
                    "Closed",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        override fun onMessage(gameid: String) {
            if(gameid == game.gameID){
                val intent = Intent(this@Host_WaitingActivity, Game_LaunchingActivity::class.java);
                startActivity(intent)
            }

        }
    }
}