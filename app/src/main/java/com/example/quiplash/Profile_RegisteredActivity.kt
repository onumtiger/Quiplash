package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth

class Profile_RegisteredActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_registered)

        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)

        auth = FirebaseAuth.getInstance()


        btnHome.setOnClickListener() {
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent);
        }

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }
    }

    fun signOut() {
        auth?.signOut()
        startActivity(Intent(this@Profile_RegisteredActivity, SignInActivity::class.java))
        finish()
    }
}
