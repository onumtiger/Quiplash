package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
