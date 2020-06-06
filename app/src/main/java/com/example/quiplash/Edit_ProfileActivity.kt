package com.example.quiplash

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.editUser
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Edit_ProfileActivity : AppCompatActivity() {

    //FirebaseAuth object
    //private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEditPicture = findViewById<Button>(R.id.btnPrrofilePic)
        val btnChangeRest = findViewById<Button>(R.id.edit_rest)
        // TO DO: load userinformation
        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        var viewUsername: EditText = findViewById(R.id.usernameFieldGuest)
        var viewEmail: EditText = findViewById(R.id.email)
        var viewPassword: EditText = findViewById(R.id.password)

        val userinfo = getUserInfo()
        viewUsername.hint = userinfo[0]
        viewEmail.hint = userinfo[1]
        viewPassword.hint = userinfo[2]

        btnBack.setOnClickListener() {
            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }

        btnEditPicture.setOnClickListener() {
            // TO DO: edit profile pic
        }

        btnChangeRest.setOnClickListener() {
            val intent = Intent(this, Edit_PW_Mail_Activity::class.java);
            startActivity(intent);
        }

        btnSave.setOnClickListener() {
            val username = viewUsername.text.toString()
            val email = viewEmail.text.toString()
            val password = viewPassword.text.toString()
            val ID = auth.currentUser?.uid.toString()

            val user = User(ID, username, false, 0)
            if (username.isEmpty() == false) {

                if (ID != null) {
                    editUser(ID, user)
                }
            } else {
                Toast.makeText(this, "please tip in a new username", Toast.LENGTH_LONG).show()
            }
        }
    }


    // TO DO: GET USER INFO
    fun getUserInfo(): Array<String> {
        var username: String = "No Username found"
        var email: String = "No Email found"
        var password: String = "••••••••••••"

        val userinfo = arrayOf(
            username,
            email,
            password
        )

        return userinfo
    }

    // TO DO: SET USER INFO
    fun setUserInfo(username: String, email: String, password: String) {

    }
    //setUserInfo(username, email, password)
}