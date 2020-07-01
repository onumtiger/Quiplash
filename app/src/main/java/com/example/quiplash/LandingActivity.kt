package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.DBMethods.DBCalls.Companion.addToken
import com.example.quiplash.DBMethods.DBCalls.Companion.getActiveGames
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    lateinit var currentUser: UserQP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_landing)

        val btnNewGame = findViewById<Button>(R.id.landing_new_game)
        val btnJoinGame = findViewById<Button>(R.id.landing_join_game)
        val btnProfile = findViewById<Button>(R.id.landing_profile)
        val btnFriends = findViewById<Button>(R.id.landing_friends)
        val btnScoreBoard = findViewById<Button>(R.id.landing_scoreboard)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefreshInvitations)

        showInvitationsHint()

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            showInvitationsHint()
            refreshLayout.isRefreshing = false
        }

        //addToken
        val callbackGetUser = object: Callback<UserQP> {
            override fun onTaskComplete(result :UserQP) {
                currentUser = result
                if (currentUser.token.isNullOrEmpty()){
                    addToken(currentUser)
                }
            }
        }
        DBMethods.DBCalls.getUser(callbackGetUser)

        btnNewGame.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, New_GameActivity::class.java)
            startActivity(intent)
        }

        btnJoinGame.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, Join_GameActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnFriends.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }

        btnScoreBoard.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, GlobalScoreboard_Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        println("do nothing")
    }

    private fun showInvitationsHint() {
        var allGames: MutableList<Game> = mutableListOf<Game>()
        var numInvitations = 0
        val invitations = findViewById<TextView>(R.id.invitations)
        val callbackInvitations = object : Callback<MutableList<Game>> {
            override fun onTaskComplete(result: MutableList<Game>) {
                allGames = result
                allGames.forEach {
                    if (it.invitations.contains(auth?.currentUser?.uid.toString())) {
                        numInvitations += 1
                    }
                }

                if (numInvitations == 0){
                    invitations.visibility = View.INVISIBLE
                } else {
                    invitations.visibility = View.VISIBLE
                    invitations.text = numInvitations.toString()
                }
            }
        }
        getActiveGames(callbackInvitations, allGames)
    }
}
