package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.database.DBMethods.Companion.addToken
import com.example.quiplash.database.DBMethods.Companion.getActiveGames
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.Game
import com.example.quiplash.game.JoinGameActivity
import com.example.quiplash.game.NewGameActivity
import com.example.quiplash.scoreboard.GlobalScoreboardActivity
import com.example.quiplash.user.friends.FriendsActivity
import com.example.quiplash.user.profile.ProfileActivity
import com.example.quiplash.user.UserQP
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
        val invitations = findViewById<TextView>(R.id.invitations)
        val logoLanding = findViewById<ImageView>(R.id.imageLogo)

        val splashanim = AnimationUtils.loadAnimation(this, R.anim.shakeback_splash)
        logoLanding.startAnimation(splashanim)
        /*set Invitations Notification Hint invisible first,
        then check if user has invitations */
        invitations.visibility = View.INVISIBLE
        showInvitationsHint()

        //addToken
        val callbackGetUser = object:
            Callback<UserQP> {
            override fun onTaskComplete(result : UserQP) {
                currentUser = result
                if (currentUser.token.isNullOrEmpty()){
                    addToken(currentUser)
                }
            }
        }
        DBMethods.getUser(callbackGetUser)

        //set clickListeners for all buttons & refreshListener for view
        btnNewGame.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, NewGameActivity::class.java)
            startActivity(intent)
        }

        btnJoinGame.setOnClickListener {
            Sounds.playClickSound(this)
            val intent = Intent(this, JoinGameActivity::class.java)
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
            val intent = Intent(this, GlobalScoreboardActivity::class.java)
            startActivity(intent)
        }

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            showInvitationsHint()
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * disable backButton on device
     */
    override fun onBackPressed() {
        println("do nothing")
    }

    /**
     * Check if user has invitations to games,
     * if true show them on Join Button
     */
    private fun showInvitationsHint() {
        var allGames: MutableList<Game> = mutableListOf<Game>()
        var numInvitations = 0
        val invitations = findViewById<TextView>(R.id.invitations)
        val callbackInvitations = object :
            Callback<MutableList<Game>> {
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
