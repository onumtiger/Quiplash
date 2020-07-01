package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.addToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId


class LandingActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    lateinit var current_User: UserQP

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

        //addToken
        val callbackGetUser = object: Callback<UserQP> {
            override fun onTaskComplete(result :UserQP) {
                current_User = result
                Toast.makeText(this@LandingActivity, current_User.token, Toast.LENGTH_SHORT).show()
                if (current_User.token.isNullOrEmpty()){
                    addToken2(current_User)
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
            val intent = Intent(this, Profile_RegisteredActivity::class.java)
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

    fun addToken2(user_t: UserQP){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val tokenNew = task.result?.token
                FirebaseInstanceId.getInstance().instanceId
                user_t.token = tokenNew.toString()
                DBMethods.DBCalls.db.collection(DBMethods.DBCalls.usersPath).document(user_t.userID)
                    .update("token", tokenNew)
                    .addOnSuccessListener { Log.d("SUCCESS", "Token successfully updated!") }
                    .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
            })
    }
}
