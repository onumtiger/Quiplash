package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth

class Edit_ProfileActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEditPicture = findViewById<Button>(R.id.btnPrrofilePic)
        // TO DO: load userinformation
        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        var viewUsername : EditText = findViewById(R.id.username)
        var viewEmail: EditText = findViewById(R.id.email)
        var viewPassword: EditText = findViewById(R.id.password)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnEditPicture.setOnClickListener(){
           // TO DO: edit profile pic
       }

        btnSave.setOnClickListener() {
            val username = viewUsername.text.toString()
            val email = viewEmail.text.toString()
            val password = viewPassword.text.toString()

            // TO DO: Save user data to firebase

            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }
    }
}