package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Firebase auth instance
        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        authListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // launch login activity
                startActivity(Intent(this@MainActivity, HomeScreen::class.java))
                finish()
            } else{
                startActivity(Intent(this@MainActivity, SignIn::class.java))
                finish()
            }
        }

        setContentView(R.layout.activity_main)

        val btnSignIn = findViewById<Button>(R.id.sign_in)
        val btnSignUp = findViewById<Button>(R.id.sign_up)
        val btnStart = findViewById<Button>(R.id.start)



        btnSignIn.setOnClickListener() {
            val intent = Intent(this, SignIn::class.java);
            startActivity(intent);
        }

        btnSignUp.setOnClickListener() {
            val intent = Intent(this, SignUp::class.java);
            startActivity(intent);
        }

        btnStart.setOnClickListener() {
            val intent = Intent(this, HomeScreen::class.java);
            startActivity(intent);
        }
    }

    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
    }


    override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }
}
